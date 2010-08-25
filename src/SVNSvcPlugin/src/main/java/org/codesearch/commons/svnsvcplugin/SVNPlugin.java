package org.codesearch.commons.svnsvcplugin;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.codesearch.commons.configreader.xml.PropertyManager;
import org.codesearch.commons.plugins.Plugin;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;
import org.springframework.stereotype.Component;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

/**
 * A plugin used to access files stored in an SVN-Repository.
 * @author David Froehlich
 */
@Component
public class SVNPlugin implements Plugin, VersionControlPlugin {

    /** The id that is used to access the username specified for SVN-repository access, must represent a single-line-property in the codesearch_config.xml file */
    public final String SVNUSERNAMEKEY = "svn_username";
    /** The id that is used to access the password specified for SVN-repository access, must represent a single-line-property in the codesearch_config.xml file */
    public final String SVNPASSWORDKEY = "svn_password";
    /** The repository that is currently accessed by this instance of SVNPlugin */
    private SVNRepository repository;
    /** The instance of the PropertyManager that is used to to retrieve the codesearch_config.xml data */
    private PropertyManager p;

    /**
     * Represents the protocol that is used to access the repositories
     * SVN and SSH both represent the same type and are only listed separately to prevent confusion
     */
    public enum SVNRepositoryProtocol {
        HTTP,
        SVN,
        SSH,
        FILE
    }

    /**
     * Creates a new instance of SVNPlugin and sets up the Factory used for the repository access depending on the given type
     * @param type the protocol used for accessing the repositories
     */
    public SVNPlugin(SVNRepositoryProtocol protocol) {
        p = new PropertyManager();
        switch (protocol){
            case HTTP: DAVRepositoryFactory.setup(); break;
            case SVN: SVNRepositoryFactoryImpl.setup(); break;
            case SSH: SVNRepositoryFactoryImpl.setup(); break;
            case FILE: FSRepositoryFactory.setup(); break;

        }
    }

    /**
     * Returns the purpose of this plugin
     * Can be used to distinguish between multiple plugins that have the same base-functionality but are used in different situations
     * Note that the return type must match the 'protocol' tag of a repository in the codesearch_config.xml file to ensure it is used for the specified repository
     * @return 'SVN'
     */
    @Override
    public String getPurpose() {
        return "SVN";
    }

    /**
     * Returns the version of the plugin
     * @return the version of the plugin
     */
    @Override
    public String getVersion() {
        return "0.1";
    }
//TODO solve the try/catch/throw problem

    /**
     * Sets the current repository base URL.
     * It is required that the URL is set before calling any of the
     * other Version Control Plugin methods.
     * @param url The URL
     */
    @Override
    public void setRepositoryURL(String url) {
        try {
            SVNURL svnurl = SVNURL.parseURIDecoded(url);
            repository = SVNRepositoryFactory.create(svnurl);

            String uname;
            String password;
            try {
                uname = p.getSingleLinePropertyValue(SVNUSERNAMEKEY);
                password = p.getSingleLinePropertyValue(SVNPASSWORDKEY);
                ISVNAuthenticationManager authManager = new BasicAuthenticationManager(uname, password);
                repository.setAuthenticationManager(authManager);
            } catch (ConfigurationException ex) {
                Logger.getLogger(SVNPlugin.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SVNException ex) {
            Logger.getLogger(SVNPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Retrieves the file content for the given file path.
     * @param filePath The file path relative to the current repository URL
     * @return The retrieved file content
     */
    @Override
    public String getFileContentForFilePath(String filePath) {
        try {
            SVNNodeKind nodeKind = repository.checkPath(filePath, -1);
            if(nodeKind != SVNNodeKind.FILE){
                return "";
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            repository.getFile(filePath, -1, null, baos);
            return baos.toString();
        } catch (SVNException ex) {
            Logger.getLogger(SVNPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    /**
     * Returns a list of changed file paths since the given revision.
     * @param revision The given revision
     * @return The paths of the changed files
     */
    @Override
    public Set<String> getPathsForChangedFilesSinceRevision(String revision) {
        Set<String> paths = new HashSet();

        Collection logs;
        try {
            logs = repository.log(new String[]{}, null, Integer.parseInt(revision), -1, true, false);
            Iterator iter = logs.iterator();
            while (iter.hasNext()) {
                SVNLogEntry entry = (SVNLogEntry) iter.next();
                Iterator iter2 = entry.getChangedPaths().keySet().iterator();
                while (iter2.hasNext()) {
                    paths.add((String) iter2.next());
                }
            }
        } catch (SVNException ex) {
            Logger.getLogger(SVNPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
        return paths;
    }
}
