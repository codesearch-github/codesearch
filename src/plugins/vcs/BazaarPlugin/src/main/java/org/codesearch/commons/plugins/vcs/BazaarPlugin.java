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

import org.codesearch.commons.configuration.xml.dto.RepositoryDto;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.vcs.bazaar.client.BazaarClientFactory;
import org.vcs.bazaar.client.BazaarRevision;
import org.vcs.bazaar.client.BazaarStatusKind;
import org.vcs.bazaar.client.IBazaarClient;
import org.vcs.bazaar.client.IBazaarLogMessage;
import org.vcs.bazaar.client.IBazaarStatus;
import org.vcs.bazaar.client.commandline.CommandLineClientFactory;
import org.vcs.bazaar.client.commandline.commands.options.Option;
import org.vcs.bazaar.client.core.BazaarClientException;
import org.vcs.bazaar.client.core.BranchLocation;

/**
 * A plugin used to access files stored in Bazaar repositories.
 * @author Stephan Stiboller
 */
public class BazaarPlugin implements VersionControlPlugin {

    private static final Logger LOG = Logger.getLogger(BazaarPlugin.class);
    private IBazaarClient bazaarClient;
    private File cacheDirectory = new File("/tmp/codesearch/bzr/");
    private BranchLocation branchLocation;
    private File branchDirectory;

    /**
     * Creates a new instance of the BazaarPlugin
     */
    public BazaarPlugin() {
        try {
            cacheDirectory.mkdirs();
            CommandLineClientFactory.setup(true);
            BazaarClientFactory.setPreferredClientType(CommandLineClientFactory.CLIENT_TYPE);
            BazaarClientFactory.setupBestAvailableBackend();
            this.bazaarClient = BazaarClientFactory.createClient(CommandLineClientFactory.CLIENT_TYPE);
        } catch (BazaarClientException ex) {
            LOG.error("Could not initialize Bazaar VCS plugin: " + ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setRepository(RepositoryDto repo) throws VersionControlPluginException {
        try {
            branchLocation = new BranchLocation(repo.getUrl());
            branchDirectory = new File(cacheDirectory.getAbsolutePath() + "/" + repo.getName());
            if (branchDirectory.isDirectory()) {
                LOG.debug("Specified repository already checked out at: " + branchDirectory.getAbsolutePath());
                LOG.debug("Trying to update local branch...");
                bazaarClient.update(branchDirectory);
            } else {
                LOG.debug("Checking out repository " + repo.getName() + " to: " + branchDirectory.getAbsolutePath());
                if (repo.getUsedAuthentication() instanceof BasicAuthentication) {
                    URI uri = branchLocation.getURI();
                    BasicAuthentication ba = (BasicAuthentication) repo.getUsedAuthentication();
                    URI newURI = new URI(uri.getScheme(), ba.getUsername() + ":" + ba.getPassword(), uri.getPath(), uri.getQuery(), uri.getFragment());
                    LOG.debug("Authentication URI: " + newURI);
                    branchLocation = new BranchLocation(newURI);
                    bazaarClient.branch(branchLocation, new File("/tmp/bzr"), bazaarClient.revno(branchLocation));
                } else if (repo.getUsedAuthentication() instanceof SshAuthentication) {
                    //TODO add external ssh auth
                    LOG.error("SSH authentication not yet supported");
                } else if (repo.getUsedAuthentication() instanceof NoAuthentication) {
                    bazaarClient.branch(branchLocation, branchDirectory, null);
                }
                LOG.debug("Finished checkout");
            }
        } catch (BazaarClientException ex) {
            throw new VersionControlPluginException("Could not checkout specified branch: " + ex);
        } catch (URISyntaxException ex) {
            throw new VersionControlPluginException("Invalid URI: " + ex);
        } catch (NullPointerException ex) {
            throw new VersionControlPluginException("BazaarUtil was never initialized, please check your installation of bzr-xmloutput >= 0.6.0 plugin");
        }
    }

    /** {@inheritDoc} */
    @Override
    public FileDto getFileForFilePath(String filePath) throws VersionControlPluginException {
        try {
            int revisionNumber = Integer.parseInt(getRepositoryRevision());
            LOG.debug("Retrieving revision " + revisionNumber + " of file: " + filePath);

            BazaarRevision bRevision = BazaarRevision.getRevision(revisionNumber);
            InputStream in = bazaarClient.cat(new File(filePath), bRevision);
            return new FileDto(filePath, IOUtils.toByteArray(in), true);
        } catch (BazaarClientException ex) {
            throw new VersionControlPluginException(ex.toString());
        } catch (IOException ex) {
            throw new VersionControlPluginException(ex.toString());
        }
    }

    /** {@inheritDoc} */
    @Override
    public Set<FileDto> getChangedFilesSinceRevision(String revision) throws VersionControlPluginException {
        try {
            LOG.info("Getting changes since revision " + revision + " for repository at " + branchDirectory);
            Set<FileDto> files = new HashSet<FileDto>();
            List<IBazaarLogMessage> iblm = getChangesSinceRevison(revision);
            LOG.info(iblm.size() + " new revisions");
            for (IBazaarLogMessage log : iblm) {
                LOG.debug("Getting changes for revision: " + log.getRevision());
                for (IBazaarStatus bs : log.getAffectedFiles(true)) {
                    LOG.debug("File " + bs.getPath() + " has status: " + bs.getShortStatus());

                    if (bs.contains(BazaarStatusKind.CREATED) || bs.contains(BazaarStatusKind.MODIFIED)) {
                        LOG.debug("File changed: " + bs.getPath());
                        files.add(getFileForFilePath(bs.getPath()));
                    } else if (bs.contains(BazaarStatusKind.RENAMED)) {
                        LOG.debug("File moved: " + bs.getPreviousPath() + " => " + bs.getPath());
                        FileDto file = new FileDto();
                        file.setFilePath(bs.getPreviousPath());
                        file.setDeleted(true);
                        files.add(file);
                        files.add(getFileForFilePath(bs.getPath()));
                    } else if (bs.contains(BazaarStatusKind.DELETED)) {
                        LOG.debug("File deleted: " + bs.getFile().getPath());
                        FileDto file = new FileDto();
                        file.setFilePath(bs.getFile().getPath());
                        file.setDeleted(true);
                    }
                }
            }
            return files;
        } catch (BazaarClientException ex) {
            throw new VersionControlPluginException(ex.toString());
        } catch (Exception ex) {
            throw new VersionControlPluginException(ex.toString());
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getRepositoryRevision() throws VersionControlPluginException {
        try {
            return bazaarClient.revno(branchDirectory).toString();
        } catch (BazaarClientException ex) {
            throw new VersionControlPluginException(ex.toString());
        }
    }

    /**
     * Retrieves the information about the change details since the specified
     * revision
     * @param uri
     * @param revision
     * @return List of IBazaarLogMessages
     * @throws BazaarClientException
     * @throws URISyntaxException
     */
    private List<IBazaarLogMessage> getChangesSinceRevison(String revision) throws BazaarClientException {

            List<Option> options = new ArrayList<Option>();
            if (!(revision == null || revision.equalsIgnoreCase("0"))) {
                options.add(new Option("-r" + revision + ".."));
                options.add(new Option("--version"));
            }
            Option[] optionArray = options.toArray(new Option[options.size()]);
            List<IBazaarLogMessage> logList = bazaarClient.log(branchDirectory, optionArray);
            if (logList == null) {
                logList = new LinkedList<IBazaarLogMessage>();
            }
            return logList;
    }

    /** {@inheritDoc} */
    @Override
    public String getPurposes() {
        return "BZR";
    }

    /** {@inheritDoc} */
    @Override
    public String getVersion() {
        return "0.1";
    }

    @Override
    public List<String> getFilesInDirectory(String directoryPath) throws VersionControlPluginException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCacheDirectory(String directoryPath) throws VersionControlPluginException {
        this.cacheDirectory = new File(directoryPath);
        if (!cacheDirectory.isDirectory() && cacheDirectory.canWrite()) {
            throw new VersionControlPluginException("Invalid cache directory specified: " + directoryPath);
        }
    }
}
