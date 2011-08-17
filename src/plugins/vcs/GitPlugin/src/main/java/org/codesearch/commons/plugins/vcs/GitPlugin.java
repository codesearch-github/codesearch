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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.vcs;

import org.codesearch.commons.configuration.xml.dto.RepositoryDto;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.codesearch.commons.plugins.vcs.utils.GitUtils;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;

/**
 *
 * @author zeheron
 */
public class GitPlugin implements VersionControlPlugin {

    private static final Logger LOG = Logger.getLogger(GitPlugin.class);
    private Repository currentGitRepository;
    private RepositoryDto currentRepository;
    private GitUtils gitUtil;
    private File cacheDirectory = new File("/tmp/codesearch/git/");

    /**
     * Sets up a new Instance of the GitPlugin
     */
    public GitPlugin() {
        gitUtil = GitUtils.getInstance();
    }

    /** {@inheritDoc} */
    @Override
    public void setRepository(RepositoryDto repo) throws VersionControlPluginException {
        this.currentRepository = repo;
        LOG.debug("Git repository set to: " + currentRepository.getName());
        try {
            RepositoryBuilder builder = new RepositoryBuilder();
            builder.setWorkTree(new File(cacheDirectory, currentRepository.getName()));
            builder.readEnvironment(); // scans environment GIT_* variables
            builder.findGitDir();
            builder.setup();
            currentGitRepository = builder.build();
        } catch (IllegalArgumentException ex) {
            throw new VersionControlPluginException(ex.toString());
        } catch (IOException ex) {
            throw new VersionControlPluginException(ex.toString());
        }
    }

    /** {@inheritDoc} */
    @Override
    public Set<FileDto> getChangedFilesSinceRevision(String revision) throws VersionControlPluginException {
        try {
            return gitUtil.getChangedFilesSinceRevision(currentGitRepository, revision);
        } catch (IOException ex) {
            throw new VersionControlPluginException(ex.toString());
        }
    }

    /** {@inheritDoc} */
    @Override
    public FileDto getFileForFilePath(String filePath) throws VersionControlPluginException {
        try {
            return gitUtil.retrieveFile(currentGitRepository, filePath, Integer.valueOf(getRepositoryRevision()));
        } catch (AmbiguousObjectException ex) {
            throw new VersionControlPluginException(ex.toString());
        } catch (MissingObjectException ex) {
            throw new VersionControlPluginException(ex.toString());
        } catch (IOException ex) {
            throw new VersionControlPluginException(ex.toString());
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getRepositoryRevision() throws VersionControlPluginException {
        try {
            return currentGitRepository.resolve(Constants.HEAD).getName();
        } catch (AmbiguousObjectException ex) {
            throw new VersionControlPluginException("" + ex);
        } catch (IOException ex) {
            throw new VersionControlPluginException("" + ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getPurposes() {
        return "GIT";
    }

    /** {@inheritDoc} */
    @Override
    public String getVersion() {
        return "0.1";
    }

    @Override
    public List<String> getFilesInDirectory(String directoryPath) throws VersionControlPluginException {
        throw new UnsupportedOperationException("Not supported yet."); //TODO impl
    }

    @Override
    public void setCacheDirectory(String directoryPath) throws VersionControlPluginException {
    }
}
