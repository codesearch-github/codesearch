package org.codesearch.commons.configuration.xml;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.configuration.dto.JobDto;
import org.codesearch.commons.configuration.dto.RepositoryDto;

/**
 *
 * @author david
 */
public class NoOpConfigurationReader implements ConfigurationReader{

    @Override
    public List<JobDto> getJobs() {
        return null;
    }

    @Override
    public List<RepositoryDto> getRepositories() {
        return null;
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
        return null;
    }

    @Override
    public File getCacheDirectory() {
        return null;
    }

    @Override
    public File getIndexLocation() {
        return null;
    }

    @Override
    public URI getSearcherLocation() {
        return null;
    }

    @Override
    public void refresh() {

    }

}
