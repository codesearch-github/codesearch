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
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codesearch.commons.configuration.dto.*;
import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
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
import org.tmatesoft.svn.core.wc.ISVNDiffStatusHandler;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNDiffStatus;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

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
    public FileDto getFile(FileIdentifier identifier, String revision)
            throws VersionControlPluginException, VcsFileNotFoundException {
        try {
            String filePath = identifier.getFilePath();
            LOG.debug("Retrieving and checking file: " + filePath);
            FileDto fileDto = new FileDto();
            SVNNodeKind nodeKind = getSvnRepo().checkPath(filePath, -1);
            if (nodeKind != SVNNodeKind.FILE) {
                throw new VcsFileNotFoundException("The file is either a folder or has been deleted");
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            SVNProperties properties = new SVNProperties();
            getSvnRepo().getFile(filePath, Long.parseLong(revision), properties, baos);
            boolean binary = !SVNProperty.isTextMimeType(properties.getStringValue(SVNProperty.MIME_TYPE));
            String lastAuthor = properties.getStringValue(SVNProperty.LAST_AUTHOR);
            String lastAlteration = properties.getStringValue(SVNProperty.COMMITTED_REVISION);
            fileDto.setLastAuthor(lastAuthor);
            fileDto.setLastAlteration(lastAlteration);
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
    public Set<FileIdentifier> getChangedFilesSinceRevision(String revision, List<String> blacklistPatterns,
            List<String> whitelistPatterns) throws VersionControlPluginException {
        List<Pattern> compiledBlacklist = new LinkedList<Pattern>();
        List<Pattern> compiledWhitelist = new LinkedList<Pattern>();
        for (String s : blacklistPatterns) {
            compiledBlacklist.add(Pattern.compile(s));
        }
        for (String s : whitelistPatterns) {
            compiledWhitelist.add(Pattern.compile(s));
        }
        Set<FileIdentifier> fileIdentifiers = new HashSet<FileIdentifier>();
        try {
            if (revision.equals(VersionControlPlugin.UNDEFINED_VERSION)) {
                if (shouldFileBeIncluded(entryPoint, compiledWhitelist, compiledWhitelist)) {
                    listEntries(entryPoint, fileIdentifiers, compiledBlacklist, compiledWhitelist);
                }
            } else {
                ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
                SVNDiffClient diffClient = new SVNDiffClient(getSvnRepo().getAuthenticationManager(), options);
                diffClient.doDiffStatus(getSvnRepo().getLocation(), SVNRevision.create(Long.parseLong(revision)),
                    getSvnRepo().getLocation(), SVNRevision.HEAD, SVNDepth.INFINITY, true, new DiffStatusHandler(
                        fileIdentifiers));
            }
        } catch (NullPointerException e) {
            throw new VersionControlPluginException("No repository specified", e);
        } catch (SVNException ex) {
            return fileIdentifiers;
        } catch (NumberFormatException ex) {
            throw new VersionControlPluginException("Invalid revision specifed", ex);
        }
        return fileIdentifiers;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getRepositoryRevision() throws VersionControlPluginException {
        try {
            return Long.toString(getSvnRepo().getLatestRevision());
        } catch (SVNException ex) {
            throw new VersionControlPluginException(ex.toString());
        }
    }

    @Override
    public List<String> getFilesInDirectory(String directoryPath, String revision) throws VersionControlPluginException {
        List<String> fileNames = new LinkedList<String>();
        try {
            Collection entries = getSvnRepo().getDir(SVNPathUtil.append(entryPoint, directoryPath), -1, null,
                (Collection)null);
            for (Object o : entries) {
                SVNDirEntry dirEntry = (SVNDirEntry)o;
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

    private void listEntries(String path, Set<FileIdentifier> identifiers, List<Pattern> blacklistPatterns,
            List<Pattern> whitelistPatterns) throws SVNException {
        Collection entries = getSvnRepo().getDir(path, -1, null, (Collection)null);
        Iterator iterator = entries.iterator();
        while (iterator.hasNext()) {
            boolean checkFile = true;
            SVNDirEntry entry = (SVNDirEntry)iterator.next();
            String fileUrl = path.equals("") ? entry.getName() : path + "/" + entry.getName();

            if (shouldFileBeIncluded(fileUrl, blacklistPatterns, whitelistPatterns)) {
                if (entry.getKind() == SVNNodeKind.DIR) {
                    listEntries(fileUrl, identifiers, blacklistPatterns, whitelistPatterns);
                } else if (entry.getKind() == SVNNodeKind.FILE) {
                    String replacedUrl = fileUrl;
                    if (StringUtils.isNotBlank(entryPoint)) {
                        replacedUrl = fileUrl.replace(entryPoint + "/", "");
                    }
                    identifiers.add(new FileIdentifier(replacedUrl, false, repository));
                }
            }
        }
    }

    private boolean shouldFileBeIncluded(String fileUrl, List<Pattern> blacklistPatterns,
            List<Pattern> whitelistPatterns) {
        boolean matchesElementOnWhitelist = false;
        boolean shouldFileBeIncluded = true;
        // if no whitelist is specified all files pass the whitelist check
        if (whitelistPatterns.isEmpty()) {
            matchesElementOnWhitelist = true;
        } else {
            // else check if the filename matches one of the whitelist entries
            for (Pattern p : whitelistPatterns) {
                Matcher m = p.matcher(fileUrl);
                if (m.find()) {
                    matchesElementOnWhitelist = true;
                    break;
                }
            }
        }
        // check if the filename matches one of the blacklist entries, if yes return false
        if (matchesElementOnWhitelist) {
            for (Pattern p : blacklistPatterns) {
                Matcher m = p.matcher(fileUrl);
                if (m.find()) {
                    shouldFileBeIncluded = false;
                    break;
                }
            }
        }
        return shouldFileBeIncluded && matchesElementOnWhitelist;
    }

    private void establishConnectionToRepo() throws VersionControlPluginException {
        if (repository == null) {
            throw new VersionControlPluginException(
                "No repository specified, you need to specify a repository before calling the validate method");
        }
        try {
            createSvnRepo(repository);

            String repoUrl = getSvnRepo().getRepositoryRoot(true).toDecodedString();
            if (!repository.getUrl().equals(repoUrl)) {
                entryPoint = repository.getUrl().replace(repoUrl, "");
                if (entryPoint.equals("/")) {
                    entryPoint = "";
                }
            }
        } catch (SVNException ex) {
            throw new VersionControlPluginException("Connection to specified repository could not be established!", ex);
        }
    }

    @Override
    public void validate() {
        // TODO add validation logic
    }

    @Override
    public void pullChanges() throws VersionControlPluginException {
        // Not needed by this plugin.
    }

    protected SVNRepository getSvnRepo() {
        return svnRepo;
    }

    protected void createSvnRepo(RepositoryDto repository) throws SVNException, VersionControlPluginException {
        SVNURL svnurl = SVNURL.parseURIDecoded(repository.getUrl());
        this.svnRepo = SVNRepositoryFactory.create(svnurl);
        AuthenticationType type = repository.getUsedAuthentication();

        if (type instanceof NoAuthentication) {
            // Nothing needs to be done
        } else if (type instanceof BasicAuthentication) {
            BasicAuthentication basicAuth = (BasicAuthentication)type;
            authManager = new BasicAuthenticationManager(basicAuth.getUsername(), basicAuth.getPassword());
            getSvnRepo().setAuthenticationManager(authManager);
        } else if (type instanceof SshAuthentication) {
            SshAuthentication sshAuth = (SshAuthentication)type;
            File sshFile = new File(sshAuth.getSshFilePath());
            if (sshFile.exists() && sshFile.canRead()) {
                authManager = new BasicAuthenticationManager(sshAuth.getUsername(), sshFile, sshAuth.getPassword(),
                    Integer.parseInt(sshAuth.getPort()));
                getSvnRepo().setAuthenticationManager(authManager);
            } else {
                throw new VersionControlPluginException("SSH key file could not be read");
            }
        }
        getSvnRepo().testConnection();
    }

    private class DiffStatusHandler implements ISVNDiffStatusHandler {

        private Set<FileIdentifier> fileIdentifiers;

        public DiffStatusHandler(Set<FileIdentifier> fileIdentifiers) {
            this.fileIdentifiers = fileIdentifiers;
        }

        @Override
        public void handleDiffStatus(SVNDiffStatus diffStatus) throws SVNException {
            if (diffStatus.getKind() == SVNNodeKind.FILE) {
                boolean deleted = diffStatus.getModificationType() == SVNStatusType.STATUS_DELETED;
                fileIdentifiers.add(new FileIdentifier(diffStatus.getPath(), deleted, repository));
            }
        }
    }

    private class ListDirectoryDirEntryHandler implements ISVNDirEntryHandler {

        private Set<FileIdentifier> fileIdentifiers;

        public ListDirectoryDirEntryHandler(Set<FileIdentifier> fileIdentifiers) {
            this.fileIdentifiers = fileIdentifiers;
        }

        @Override
        public void handleDirEntry(SVNDirEntry dirEntry) throws SVNException {
            fileIdentifiers.add(new FileIdentifier(dirEntry.getRelativePath(), false, repository));
        }
    }
}
