/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.vcs;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.plugins.vcs.utils.GitUtils;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;

/**
 *
 * @author zeheron
 */
public class GitPlugin implements VersionControlPlugin {

    private Repository repository;
    private GitUtils gitUtil;
    private static final Logger LOG = Logger.getLogger(GitPlugin.class);

    /**
     * Sets up a new Instance of the GitPlugin
     */
    public GitPlugin() {
        gitUtil = GitUtils.getInstance();
    }

    /** {@inheritDoc} */
    @Override
    public void setRepository(RepositoryDto repo) throws VersionControlPluginException {
        try {
            LOG.debug("Git repository set to: " + repository);
            RepositoryBuilder builder = new RepositoryBuilder();
            builder.setGitDir(new File(repo.getUrl()));
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

    /** {@inheritDoc} */
    @Override
    public Set<FileDto> getChangedFilesSinceRevision(String revision) throws VersionControlPluginException {
        try {
            return gitUtil.getChangedFilesSinceRevision(repository, revision);
        } catch (IOException ex) {
            throw new VersionControlPluginException(ex.toString());
        }
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
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
}
