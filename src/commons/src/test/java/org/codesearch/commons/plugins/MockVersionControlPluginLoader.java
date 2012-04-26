package org.codesearch.commons.plugins;

import java.util.List;
import java.util.Set;

import org.codesearch.commons.configuration.dto.RepositoryDto;
import org.codesearch.commons.plugins.vcs.FileDto;
import org.codesearch.commons.plugins.vcs.FileIdentifier;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;
import org.codesearch.commons.plugins.vcs.VersionControlPluginException;
import org.codesearch.commons.validator.ValidationException;

/**
 * Always returns a mock version control plugin.
 *
 * @author Samuel Kogler
 */
public class MockVersionControlPluginLoader implements PluginLoader {

    @Override
    public <T extends Plugin> List<T> getMultiplePluginsForPurpose(Class<T> clazz, String purpose) throws PluginLoaderException {
        return null;
    }

    @Override
    public <T extends Plugin> T getPlugin(Class<T> clazz, String purpose) throws PluginLoaderException {
        if (clazz.equals(VersionControlPlugin.class)) {
            return clazz.cast(new VersionControlPlugin() {

                @Override
                public void setRepository(RepositoryDto repository) throws VersionControlPluginException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public FileDto getFile(FileIdentifier fileInfo, String revision) throws VersionControlPluginException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Set<FileIdentifier> getChangedFilesSinceRevision(String revision, List<String> blacklistPatterns, List<String> whitelistPatterns) throws VersionControlPluginException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public String getRepositoryRevision() throws VersionControlPluginException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public List<String> getFilesInDirectory(String directoryPath, String revision) throws VersionControlPluginException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void setCacheDirectory(String directoryPath) throws VersionControlPluginException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void validate() throws ValidationException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public String getPurposes() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void pullChanges() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            });
        } else {
            return null;
        }
    }
}
