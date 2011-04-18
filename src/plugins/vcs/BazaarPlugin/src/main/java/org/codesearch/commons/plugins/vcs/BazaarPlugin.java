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
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.plugins.PluginLoaderException;
import org.codesearch.commons.plugins.vcs.utils.BazaarUtils;
import org.vcs.bazaar.client.IBazaarLogMessage;
import org.vcs.bazaar.client.IBazaarStatus;
import org.vcs.bazaar.client.core.BazaarClientException;
import org.vcs.bazaar.client.core.BranchLocation;

/**
 * A plugin used to access files stored in Bazaar repositories.
 * @author Stephan Stiboller
 */
public class BazaarPlugin implements VersionControlPlugin {

    private BazaarUtils bzr_util;
    private BranchLocation bl;
    private static final Logger LOG = Logger.getLogger(BazaarPlugin.class);

    /**
     * Creates a new instance of the BazaarPlugin
     */
    public BazaarPlugin() {
        try {
            bzr_util = BazaarUtils.getInstance();
        } catch (BazaarClientException ex) {
            LOG.error("Loading of the BazaarPlugin failed, you probably have to install Bazaar Xml Output\n"+ex);
        }
    }

     /** {@inheritDoc} */
    @Override
    public void setRepository(RepositoryDto repo) throws VersionControlPluginException {
        try {
            bl = bzr_util.createBranchLocation(repo.getUrl(), repo.getName(), repo.getPassword());
            //remove protocol pattern
            bzr_util.setWorkingDirectory("/tmp/test/asdf");
        } catch (URISyntaxException ex) {
            throw new VersionControlPluginException(ex.toString());
        } catch (NullPointerException ex){
            //in case the bzr_util was never initialized
            throw new VersionControlPluginException("BazaarUtil was never initialized, please check your installation of Bazaar Xml Output");
        }
    }

     /** {@inheritDoc} */
    @Override
    public FileDto getFileForFilePath(String filePath) throws VersionControlPluginException {
        try {
            return new FileDto(filePath, bzr_util.retrieveFileContent(new URI(filePath), getRepositoryRevision()), true);
        } catch (URISyntaxException ex) {
            throw new VersionControlPluginException(ex.toString());
        } catch (BazaarClientException ex) {
            throw new VersionControlPluginException(ex.toString());
        } catch (IOException ex) {
            throw new VersionControlPluginException(ex.toString());
        }
    }

     /** {@inheritDoc} */
    @Override
    public Set<FileDto> getChangedFilesSinceRevision(String revision) throws VersionControlPluginException {
        Set<FileDto> files = new HashSet<FileDto>();
        try {
            LOG.info("bl  " + bl.getURI().toString());
            List<IBazaarLogMessage> iblm = bzr_util.getChangesSinceRevison(bl, revision);
            for (IBazaarLogMessage log : iblm) {
                for (IBazaarStatus bs : log.getAffectedFiles()) {
                    LOG.debug("Filepath retrieved: " + bs.getFile().getAbsolutePath());
                    FileInputStream fis = new FileInputStream(bs.getFile());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte fileContent[] = new byte[(int) bs.getFile().length()];
                    fis.read(fileContent);
                    FileDto fd = new FileDto(bs.getAbsolutePath(), fileContent, false); //TODO: ADD MIME TYPE...
                    files.add(fd);
                }
            }
        } catch (BazaarClientException ex) {
            throw new VersionControlPluginException(ex.toString());
        } catch (URISyntaxException ex) {
            throw new VersionControlPluginException(ex.toString());
        } catch (Exception ex) {
            throw new VersionControlPluginException(ex.toString());
        }
        return files;
    }

     /** {@inheritDoc} */
    @Override
    public String getRepositoryRevision() throws VersionControlPluginException {
        try {
            return bzr_util.getLatestRevisionNumber(bl);
        } catch (BazaarClientException ex) {
            throw new VersionControlPluginException(ex.toString());
        }
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
}
