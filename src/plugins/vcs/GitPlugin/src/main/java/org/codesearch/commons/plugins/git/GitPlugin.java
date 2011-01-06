/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.git;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Set;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.codesearch.commons.plugins.vcs.FileDto;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;
import org.codesearch.commons.plugins.vcs.VersionControlPluginException;
import org.codesearch.commons.utils.git.GitUtils;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectStream;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.util.Base64.InputStream;

/**
 *
 * @author zeheron
 */
public class GitPlugin implements VersionControlPlugin {

    private Repository repository;
    private GitUtils gitUtil;
    private static final Logger LOG = Logger.getLogger(GitPlugin.class);

    public GitPlugin() {
        gitUtil = GitUtils.getInstance();
    }

    @Override
    public void setRepository(URI url, String username, String password) throws VersionControlPluginException {
        try {
            RepositoryBuilder builder = new RepositoryBuilder();
            builder.setGitDir(new File(url));
            builder.readEnvironment(); // scans environment GIT_* variables
            builder.findGitDir();
            builder.setup();
            repository = builder.build();
        } catch (IllegalArgumentException ex) {
            throw new VersionControlPluginException(ex.toString());
        } catch (IOException ex) {
            throw new VersionControlPluginException(ex.toString());
        }
    }

    @Override
    public Set<FileDto> getChangedFilesSinceRevision(String revision) throws VersionControlPluginException {
        return null;
    }

    @Override
    public FileDto getFileForFilePath(String filePath) throws VersionControlPluginException {
        try {
            return gitUtil.retrieveFile(repository, filePath, Integer.valueOf(getRepositoryRevision()));
        } catch (AmbiguousObjectException ex) {
            throw new VersionControlPluginException(ex.toString());
        } catch (MissingObjectException ex) {
            throw new VersionControlPluginException(ex.toString());
        } catch (IOException ex) {
            throw new VersionControlPluginException(ex.toString());
        }
    }

    @Override
    public String getRepositoryRevision() throws VersionControlPluginException {
        try {
            return String.valueOf(gitUtil.retrieveHeadRevisionForRepository(repository));
        } catch (AmbiguousObjectException ex) {
            throw new VersionControlPluginException(ex.toString());
        } catch (IOException ex) {
            throw new VersionControlPluginException(ex.toString());
        }
    }

    @Override
    public String getPurposes() {
        return "GIT";
    }

    @Override
    public String getVersion() {
        return "0.1";
    }
}
