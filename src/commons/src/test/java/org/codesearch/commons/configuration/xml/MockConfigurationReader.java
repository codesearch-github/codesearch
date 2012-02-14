package org.codesearch.commons.configuration.xml;

import java.io.File;
import java.net.URI;
import java.util.List;
import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.configuration.dto.CodesearchConfiguration;
import org.codesearch.commons.configuration.dto.DatabaseConfiguration;
import org.codesearch.commons.configuration.dto.IndexerUserDto;
import org.codesearch.commons.configuration.dto.JobDto;
import org.codesearch.commons.configuration.dto.RepositoryDto;

/**
 * Mock configuration reader, has a public configuration object that can be used
 * to set test values.
 * @author Samuel Kogler
 */
public class MockConfigurationReader implements ConfigurationReader {

    public CodesearchConfiguration configuration = new CodesearchConfiguration();

    @Override
    public List<IndexerUserDto> getIndexerUsers() {
        return configuration.getIndexerUsers();
    }

    @Override
    public List<JobDto> getJobs() {
        return configuration.getJobs();
    }

    @Override
    public List<RepositoryDto> getRepositories() {
        return configuration.getRepositories();
    }

    @Override
    public List<String> getRepositoriesForGroup(String groupName) {
        return null;
    }

    @Override
    public RepositoryDto getRepositoryByName(String name) {
        return null;
    }

    @Override
    public List<String> getRepositoryGroups() {
        return configuration.getRepositoryGroups();
    }

    @Override
    public DatabaseConfiguration getDatabaseConfiguration() {
        return configuration.getDatabaseConfiguration();
    }

    @Override
    public File getCacheDirectory() {
        return configuration.getCacheDirectory();
    }

    @Override
    public File getIndexLocation() {
        return configuration.getIndexLocation();
    }

    @Override
    public URI getSearcherLocation() {
        return configuration.getSearcherLocation();
    }

    @Override
    public void refresh() {
    }
}
