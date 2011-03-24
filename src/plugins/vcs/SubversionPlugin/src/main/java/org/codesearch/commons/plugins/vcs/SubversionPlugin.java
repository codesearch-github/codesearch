/**
 * Copyright 2010 David Froehlich   <david.froehlich@businesssoftware.at>,
 *                Samuel Kogler     <samuel.kogler@gmail.com>,
 *                Stephan Stiboller <stistc06@htlkaindorf.at>
 *
 * This file is part of Codesearch.
 *
 * Codesearch is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Codesearch is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Codesearch.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.codesearch.commons.plugins.vcs;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
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
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

/**
 * A plugin used to access files stored in Subversion repositories.
 * @author David Froehlich
 * @author Stephan Stiboller
 * @author Samuel Kogler
 */
public class SubversionPlugin implements VersionControlPlugin {

    /** The repository that is currently accessed. */
    private SVNRepository svnRepo;
    private static final Logger LOG = Logger.getLogger(SubversionPlugin.class);
    private ISVNAuthenticationManager authManager;


    /**
     * creates a new instance of SubversionPlugin and sets up all factories required for connections
     */
    public SubversionPlugin() {
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        FSRepositoryFactory.setup();
    }

    /** {@inheritDoc} */
    @Override
    public String getPurposes() {
        return "SVN";
    }

    /** {@inheritDoc} */
    @Override
    public String getVersion() {
        return "0.1";
    }

    /** {@inheritDoc} */
    @Override
    public void setRepository(RepositoryDto repository) throws VersionControlPluginException {
        try {
            SVNURL svnurl = SVNURL.parseURIDecoded(repository.getUrl().toString());
            svnRepo = SVNRepositoryFactory.create(svnurl);
            authManager = new BasicAuthenticationManager(repository.getUsername(), repository.getPassword());
            svnRepo.setAuthenticationManager(authManager);
        } catch (SVNException ex) {
            throw new VersionControlPluginException(ex.toString());
        }
    }

    /** {@inheritDoc} */
    @Override
    public FileDto getFileForFilePath(String filePath) throws VersionControlPluginException {
        try {
            LOG.debug("Retrieving file: " + filePath);
            FileDto fileDto = new FileDto();
            SVNNodeKind nodeKind = svnRepo.checkPath(filePath, -1);
            if (nodeKind != SVNNodeKind.FILE) {
                return null;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            SVNProperties properties = new SVNProperties();
            svnRepo.getFile(filePath, -1, properties, baos);
            boolean binary = !SVNProperty.isTextMimeType(properties.getStringValue(SVNProperty.MIME_TYPE));
            String lastAuthor = properties.getStringValue(SVNProperty.LAST_AUTHOR);
            String lastAlterationDate = properties.getStringValue(SVNProperty.COMMITTED_DATE); //TODO test this
            fileDto.setLastAuthor(lastAuthor);
            fileDto.setLastAlteration(lastAlterationDate);
            fileDto.setContent(baos.toByteArray());
            fileDto.setBinary(binary);
            return fileDto;
        } catch (SVNException ex) {
            throw new VersionControlPluginException(ex.toString());
        }
    }

    /** {@inheritDoc} */
    @Override
    public Set<FileDto> getChangedFilesSinceRevision(String revision) throws VersionControlPluginException {
        Set<FileDto> files = new HashSet();
        List logs = null;
        try {
            logs = (LinkedList) svnRepo.log(new String[]{}, null, Long.parseLong(revision) + 1, -1, true, false);
        } catch (NullPointerException e) {
            throw new VersionControlPluginException("No repository specified");
        } catch (SVNException ex) {
            logs = new LinkedList();
        }
        for (int i = 0; i < logs.size(); i++) {
            SVNLogEntry entry = (SVNLogEntry) logs.get(i);
            Iterator entryIterator = entry.getChangedPaths().values().iterator();
            while (entryIterator.hasNext()) {
                SVNLogEntryPath currentPath = (SVNLogEntryPath) entryIterator.next();
                String filePath = currentPath.getPath();
                if (currentPath.getType() == 'D') { //The file has been deleted
                    FileDto deletedFile = new FileDto();
                    deletedFile.setDeleted(true);
                    deletedFile.setFilePath(filePath);
                    files.add(deletedFile);
                } else {
                    boolean fileAlreadyInSet = false;
                    for (FileDto currentFile : files) {
                        if (currentFile.getFilePath().equals(filePath)) {
                            fileAlreadyInSet = true;
                        }
                    }
                    if (!fileAlreadyInSet) {
                        FileDto changedFile = getFileForFilePath(filePath);
                        if(changedFile != null){
                            files.add(changedFile);
                        }
                    }
                }
            }
        }
        return files;
    }

    /**{@inheritDoc }*/
    @Override
    public String getRepositoryRevision() throws VersionControlPluginException {
        try {
            return Long.toString(svnRepo.getLatestRevision());
        } catch (SVNException ex) {
            throw new VersionControlPluginException(ex.toString());
        }
    }

    @Override
    public List<String> getFilesInDirectory(String directoryPath) throws VersionControlPluginException {
        List<String> fileNames = new LinkedList<String>();
        try {
            directoryPath = svnRepo.getRepositoryRoot(true).getPath();
            svnRepo.getDir(directoryPath, -1, null, new ListDirectoryDirEntryHandler(fileNames));
        } catch (SVNException ex) {
            throw new VersionControlPluginException(ex.getMessage());
        }
        return fileNames;
    }

    class ListDirectoryDirEntryHandler implements ISVNDirEntryHandler {

        List<String> fileNames;

        public ListDirectoryDirEntryHandler(List<String> fileNames) {
            this.fileNames = fileNames;
        }

        @Override
        public void handleDirEntry(SVNDirEntry dirEntry) throws SVNException {
            fileNames.add(dirEntry.getRelativePath());
        }
    }
}
