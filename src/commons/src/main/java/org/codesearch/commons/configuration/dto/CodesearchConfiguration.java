/**
 * 
 */
package org.codesearch.commons.configuration.dto;

import java.io.File;
import java.net.URI;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Simple bean representing the configuration of codesearch.
 * @author Samuel Kogler
 */
@XStreamAlias("configuration")
public class CodesearchConfiguration {
    
    @XStreamAlias("searcher-location")
    private URI searcherLocation;
    @XStreamAlias("cache-directory")
    private File cacheDirectory;
    @XStreamAlias("index-directory")
    private File indexLocation;
    @XStreamAlias("indexer-users")
    private List<IndexerUserDto> indexerUsers;
    @XStreamAlias("index-jobs")
    private List<JobDto> jobs;
    private List<RepositoryDto> repositories;
    @XStreamAlias("global-whitelist")
    private List<String> globalWhitelist;
    @XStreamAlias("global-blacklist")
    private List<String> globalBlacklist;
    @XStreamAlias("repository-groups")
    private List<String> repositoryGroups;
    @XStreamAlias("database")
    private DatabaseConfiguration databaseConfiguration;
    
    public URI getSearcherLocation() {
        return searcherLocation;
    }
    public void setSearcherLocation(URI searcherLocation) {
        this.searcherLocation = searcherLocation;
    }
    public File getCacheDirectory() {
        return cacheDirectory;
    }
    public void setCacheDirectory(File cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }
    public File getIndexLocation() {
        return indexLocation;
    }
    public void setIndexLocation(File indexLocation) {
        this.indexLocation = indexLocation;
    }
    public List<IndexerUserDto> getIndexerUsers() {
        return indexerUsers;
    }
    public void setIndexerUsers(List<IndexerUserDto> indexerUsers) {
        this.indexerUsers = indexerUsers;
    }
    public List<JobDto> getJobs() {
        return jobs;
    }
    public void setJobs(List<JobDto> jobs) {
        this.jobs = jobs;
    }
    public List<RepositoryDto> getRepositories() {
        return repositories;
    }
    public void setRepositories(List<RepositoryDto> repositories) {
        this.repositories = repositories;
    }
    public List<String> getGlobalWhitelist() {
        return globalWhitelist;
    }
    public void setGlobalWhitelist(List<String> globalWhitelist) {
        this.globalWhitelist = globalWhitelist;
    }
    public List<String> getGlobalBlacklist() {
        return globalBlacklist;
    }
    public void setGlobalBlacklist(List<String> globalBlacklist) {
        this.globalBlacklist = globalBlacklist;
    }
    public List<String> getRepositoryGroups() {
        return repositoryGroups;
    }
    public void setRepositoryGroups(List<String> repositoryGroups) {
        this.repositoryGroups = repositoryGroups;
    }
    public DatabaseConfiguration getDatabaseConfiguration() {
        return databaseConfiguration;
    }
    public void setDatabaseConfiguration(DatabaseConfiguration databaseConfiguration) {
        this.databaseConfiguration = databaseConfiguration;
    }
}
