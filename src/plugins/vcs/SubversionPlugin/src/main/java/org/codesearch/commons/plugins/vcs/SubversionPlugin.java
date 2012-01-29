/**
 * Copyright 2010 David Froehlich <david.froehlich@businesssoftware.at>, Samuel
 * Kogler <samuel.kogler@gmail.com>, Stephan Stiboller <stistc06@htlkaindorf.at>
 *
 * This file is part of Codesearch.
 *
 * Codesearch is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Codesearch is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Codesearch. If not, see <http://www.gnu.org/licenses/>.
 */
package org.codesearch.commons.plugins.vcs;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.codesearch.commons.configuration.dto.AuthenticationType;
import org.codesearch.commons.configuration.dto.BasicAuthentication;
import org.codesearch.commons.configuration.dto.NoAuthentication;
import org.codesearch.commons.configuration.dto.RepositoryDto;
import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.util.SVNPathUtil;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

/**
 * A plugin used to access files stored in Subversion repositories.
 *
 * @author David Froehlich
 * @author Stephan Stiboller
 * @author Samuel Kogler
 */
public class SubversionPlugin implements VersionControlPlugin {

    private static final Logger LOG = Logger.getLogger(SubversionPlugin.class);
    /**
     * The repository that is currently accessed.
     */
    private SVNRepository svnRepo;
    private ISVNAuthenticationManager authManager;
    private RepositoryDto repository;
    private String entryPoint = "";

    /**
     * creates a new instance of SubversionPlugin and sets up all factories
     * required for connections
     */
    public SubversionPlugin() {
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        FSRepositoryFactory.setup();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPurposes() {
        return "SVN";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRepository(RepositoryDto repository) throws VersionControlPluginException {
        this.repository = repository;
        establishConnectionToRepo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileDto getFileDtoForFileIdentifierAtRevision(FileIdentifier identifier, String revision) throws VersionControlPluginException {
        try {
            String filePath = identifier.getFilePath();
            LOG.debug("Retrieving and checking file: " + filePath);
            FileDto fileDto = new FileDto();
            SVNNodeKind nodeKind = svnRepo.checkPath(filePath, -1);
            if (nodeKind != SVNNodeKind.FILE) {
                throw new VersionControlPluginException("The file is either a folder or has been deleted");
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            SVNProperties properties = new SVNProperties();
            svnRepo.getFile(filePath, Long.parseLong(revision), properties, baos);
            boolean binary = !SVNProperty.isTextMimeType(properties.getStringValue(SVNProperty.MIME_TYPE));
            String lastAuthor = properties.getStringValue(SVNProperty.LAST_AUTHOR);
            String lastAlterationDate = properties.getStringValue(SVNProperty.COMMITTED_DATE); //TODO test this
            fileDto.setLastAuthor(lastAuthor);
            fileDto.setLastAlteration(lastAlterationDate);
            fileDto.setContent(baos.toByteArray());
            fileDto.setBinary(binary);
            fileDto.setFilePath(filePath);
            fileDto.setRepository(repository);
            return fileDto;
        } catch (SVNException ex) {
            throw new VersionControlPluginException(ex.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<FileIdentifier> getChangedFilesSinceRevision(String revision) throws VersionControlPluginException {
        Set<FileIdentifier> identifiers = new HashSet<FileIdentifier>();
        try {
            if (revision.equals(VersionControlPlugin.UNDEFINED_VERSION)) {
                listEntriesRecursively(entryPoint, identifiers);
            } else {
                Collection logs = svnRepo.log(new String[]{}, null, Long.parseLong(revision) + 1, -1, true, false);
                for (Object o : logs) {
                    SVNLogEntry entry = (SVNLogEntry) o;
                    Iterator entryIterator = entry.getChangedPaths().values().iterator();
                    while (entryIterator.hasNext()) {
                        SVNLogEntryPath currentPath = (SVNLogEntryPath) entryIterator.next();

                        //attribute values for the new fileIdentifier
                        String filePath = currentPath.getPath();
                        boolean deleted = false;
                        if (currentPath.getType() == 'D') { //The file has been deleted
                            deleted = true;
                        }
                        FileIdentifier fileIdentifier = new FileIdentifier(filePath, false, deleted, repository);
                        if (identifiers.contains(fileIdentifier)) {
                            identifiers.remove(fileIdentifier);
                        }
                        identifiers.add(fileIdentifier);
                        LOG.debug("Add file to indexing list: " + filePath);
                    }
                }
            }
        } catch (NullPointerException e) {
            throw new VersionControlPluginException("No repository specified");
        } catch (SVNException ex) {
            return identifiers;
        }
        return identifiers;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getRepositoryRevision() throws VersionControlPluginException {
        try {
            return Long.toString(svnRepo.getLatestRevision());
        } catch (SVNException ex) {
            throw new VersionControlPluginException(ex.toString());
        }
    }

    @Override
    public List<String> getFilesInDirectory(String directoryPath, String revision) throws VersionControlPluginException {
        List<String> fileNames = new LinkedList<String>();
        try {
            Collection entries = svnRepo.getDir(SVNPathUtil.append(entryPoint, directoryPath), -1, null, (Collection) null);
            for (Object o : entries) {
                SVNDirEntry dirEntry = (SVNDirEntry) o;
                fileNames.add(dirEntry.getName());
            }
        } catch (SVNException ex) {
            throw new VersionControlPluginException(ex.getMessage());
        }

        return fileNames;
    }

    @Override
    public void setCacheDirectory(String directoryPath) {
        // Does not need local cache directory.
    }

    private void establishConnectionToRepo() throws VersionControlPluginException {
        if (repository == null) {
            throw new VersionControlPluginException("No repository specified, you need to specify a repository before calling the validate method");
        }
        try {
            SVNURL svnurl = SVNURL.parseURIDecoded(repository.getUrl().toString());
            svnRepo = SVNRepositoryFactory.create(svnurl);
            AuthenticationType type = repository.getUsedAuthentication();

            if (type instanceof NoAuthentication) {
                //Nothing needs to be done
            } else if (type instanceof BasicAuthentication) {
                BasicAuthentication basicAuth = (BasicAuthentication) type;
                authManager = new BasicAuthenticationManager(basicAuth.getUsername(), basicAuth.getPassword());
                svnRepo.setAuthenticationManager(authManager);
            } else {
                //TODO add support for ssh files
                throw new VersionControlPluginException("SSH authentication not yet supported.");
            }
            svnRepo.testConnection();
            String repoUrl = svnRepo.getRepositoryRoot(true).toString();
            if (!repository.getUrl().equals(repoUrl)) {
                entryPoint = repository.getUrl().replace(repoUrl, "");
                if (entryPoint.equals("/")) {
                    entryPoint = "";
                }
            }
        } catch (SVNException ex) {
            throw new VersionControlPluginException("Connection to specified repository could not be established:\n" + ex);
        }
    }

    private void listEntriesRecursively(String path, Set<FileIdentifier> identifiers) throws SVNException {
        Collection entries = svnRepo.getDir(path, -1, null, (Collection) null);
        Iterator iterator = entries.iterator();
        while (iterator.hasNext()) {
            SVNDirEntry entry = (SVNDirEntry) iterator.next();
            if (entry.getKind() == SVNNodeKind.DIR) {
                listEntriesRecursively(path + "/" + entry.getName(), identifiers);
            } else if (entry.getKind() == SVNNodeKind.FILE) {
                identifiers.add(new FileIdentifier(SVNPathUtil.getRelativePath(entryPoint, path + "/" + entry.getName()), false, false, repository));
            }
        }
    }

    @Override
    public void validate() {
        //TODO add validation logic
    }

    private class ListDirectoryDirEntryHandler implements ISVNDirEntryHandler {

        private Set<FileIdentifier> fileIdentifiers;

        public ListDirectoryDirEntryHandler(Set<FileIdentifier> fileIdentifiers) {
            this.fileIdentifiers = fileIdentifiers;
        }

        @Override
        public void handleDirEntry(SVNDirEntry dirEntry) throws SVNException {
            fileIdentifiers.add(new FileIdentifier(dirEntry.getRelativePath(), false, false, repository));
        }
    }
}
