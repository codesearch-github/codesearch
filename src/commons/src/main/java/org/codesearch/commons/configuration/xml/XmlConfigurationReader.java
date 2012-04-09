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
package org.codesearch.commons.configuration.xml;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.configuration.InvalidConfigurationException;
import org.codesearch.commons.configuration.dto.*;

/**
 * Xml implementation of the configuration reader. By default, the properties
 * are loaded from a file in the classpath called codesearch_config.xml.
 *
 * @author Stephan Stiboller
 * @author David Froehlich
 * @author Samuel Kogler
 */
@Singleton
public class XmlConfigurationReader implements ConfigurationReader {

    private static final Logger LOG = Logger.getLogger(XmlConfigurationReader.class);
    /**
     * The XMLConfiguration object that is used to read the properties from the
     * XML-file
     */
    private XMLConfiguration config;
    /**
     * The path to the configuration file.
     */
    private String configPath = "codesearch_config.xml";
    private CodesearchConfiguration codesearchConfiguration;

    /**
     * creates a new instance of XmlConfigurationReader
     *
     * @param configPath the classpath of the config file
     * @throws InvalidConfigurationException if the configuration file was
     * invalid
     */
    @Inject
    public XmlConfigurationReader(@Named("configpath") String configPath) throws InvalidConfigurationException {
        if (StringUtils.isNotEmpty(configPath)) {
            this.configPath = configPath;
        }

        LOG.debug("Reading config file: " + this.configPath);
        loadConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<IndexerUserDto> getIndexerUsers() {
        return codesearchConfiguration.getIndexerUsers();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<JobDto> getJobs() {
        return codesearchConfiguration.getJobs();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized RepositoryDto getRepositoryByName(String name) {
        for (RepositoryDto repo : codesearchConfiguration.getRepositories()) {
            if (repo.getName().equals(name)) {
                return repo;
            }
        }
        return null;
    }

    /**
     * returns a list of all globally whitelisted filenames, so every filename
     * has to match at least one of the whitelist names (only if the whitelist
     * is not empty)
     */
    public synchronized List<String> getGlobalWhitelistEntries() {
        return codesearchConfiguration.getGlobalWhitelist();
    }

    /**
     * returns a list of all file name patterns that are listed to be ignored on
     * the entire system
     */
    private synchronized List<String> getGlobalBlacklistEntries() {
        return codesearchConfiguration.getGlobalBlacklist();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<RepositoryDto> getRepositories() {
        return codesearchConfiguration.getRepositories();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<String> getRepositoryGroups() {
        return new LinkedList<String>(codesearchConfiguration.getRepositoryGroups());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<String> getRepositoriesForGroup(String groupName) {
        List<String> repos = new LinkedList<String>();
        for (RepositoryDto repo : getRepositories()) {
            if (repo.getRepositoryGroups() != null) {
                if (repo.getRepositoryGroups().contains(groupName)) {
                    repos.add(repo.getName());
                }
            }
        }
        return repos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized File getCacheDirectory() {
        return codesearchConfiguration.getCacheDirectory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized File getIndexLocation() {
        return codesearchConfiguration.getIndexLocation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized URI getSearcherLocation() {
        return codesearchConfiguration.getSearcherLocation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void refresh() {
        try {
            loadConfig();
        } catch (InvalidConfigurationException e) {
            LOG.fatal("Reload of configuration failed: \n" + e);
        }
    }

    // TODO perform config value validation here
    private void loadConfig() throws InvalidConfigurationException {
        codesearchConfiguration = new CodesearchConfiguration();
        try {
            config = new XMLConfiguration(this.configPath);
            if (config == null) {
                throw new InvalidConfigurationException("Config was null at: " + this.configPath);
            }
            config.setDelimiterParsingDisabled(true);
        } catch (ConfigurationException ex) {
            throw new InvalidConfigurationException("Configuration file could not be read:\n" + ex);
        }
        loadCacheDirectory();
        loadIndexLocation();
        loadSearcherLocation();
        loadGlobalBlacklist();
        loadGlobalWhitelist();
        loadRepositories();
        loadJobs();
        loadIndexerUsers();
    }

    private void loadCacheDirectory() throws InvalidConfigurationException {
        File cacheDirectory = null;
        try {
            cacheDirectory = new File(config.getString(XmlConfigurationReaderConstants.CACHE_DIR));
        } catch (NullPointerException ex) {
            throw new InvalidConfigurationException("Cache directory not specified");
        }
        codesearchConfiguration.setCacheDirectory(cacheDirectory);
    }

    private void loadIndexLocation() throws InvalidConfigurationException {
        File indexLocation = null;
        try {
            indexLocation = new File(config.getString(XmlConfigurationReaderConstants.INDEX_DIR));
        } catch (NullPointerException ex) {
            throw new InvalidConfigurationException("Index location not specified");
        }
        codesearchConfiguration.setIndexLocation(indexLocation);
    }

    private void loadSearcherLocation() throws InvalidConfigurationException {
        try {
            URI searcherLocation = new URI(config.getString(XmlConfigurationReaderConstants.SEARCHER_LOCATION));
            codesearchConfiguration.setSearcherLocation(searcherLocation);
        } catch (URISyntaxException ex) {
            throw new InvalidConfigurationException("Searcher location is not a valid URI: " + ex);
        }
    }

    private void loadRepositories() throws InvalidConfigurationException {
        List<RepositoryDto> repositories = new LinkedList<RepositoryDto>();
        Set<String> repositoryGroups = new HashSet<String>();

        List<HierarchicalConfiguration> repositoryConfigs = config.configurationsAt(XmlConfigurationReaderConstants.REPOSITORY_LIST);
        for (HierarchicalConfiguration repositoryConfig : repositoryConfigs) {
            RepositoryDto repo = loadRepository(repositoryConfig);
            repositoryGroups.addAll(repo.getRepositoryGroups());
            repositories.add(repo);
        }

        codesearchConfiguration.setRepositoryGroups(new LinkedList<String>(repositoryGroups));
        codesearchConfiguration.setRepositories(repositories);
    }

    /**
     * retrieves all required data about the given repository from the
     * configuration via the HierarchicalConfiguration and returns it as a
     * RepositoryDto
     */
    private RepositoryDto loadRepository(HierarchicalConfiguration hc) throws InvalidConfigurationException {
        RepositoryDto repo;
        //mandatory field
        String name = hc.getString(XmlConfigurationReaderConstants.REPOSITORY_NAME);

        // retrieve the repository blacklisted filenames and add all global filenames
        List<String> blacklistEntries = hc.getList(XmlConfigurationReaderConstants.REPOSITORY_BLACKLIST);
        if (blacklistEntries == null) {
            blacklistEntries = new LinkedList<String>();
        }
        blacklistEntries.addAll(getGlobalBlacklistEntries());

        // retrieve the repository whitelisted filenames and add all global filenames
        List<String> whitelistFileNames = hc.getList(XmlConfigurationReaderConstants.REPOSITORY_WHITELIST_FILENAMES);
        if (whitelistFileNames == null) {
            whitelistFileNames = new LinkedList<String>();
        }
        whitelistFileNames.addAll(getGlobalWhitelistEntries());

        String repoGroupString = hc.getString(XmlConfigurationReaderConstants.REPOSITORY_GROUPS);
        List<String> repositoryGroups = Arrays.asList(repoGroupString.split(" "));

        // retrieve the used authentication system and fill it with the required data
        AuthenticationType usedAuthentication = null;
        String authenticationType = hc.getString(XmlConfigurationReaderConstants.REPOSITORY_AUTHENTICATION_DATA);

        if (authenticationType == null || authenticationType.trim().isEmpty() || authenticationType.equals("none")) {
            usedAuthentication = new NoAuthentication();
        } else if (authenticationType.equals("basic")) {
            String username = hc.getString(XmlConfigurationReaderConstants.REPOSITORY_AUTHENTICATION_DATA_USERNAME);
            String password = hc.getString(XmlConfigurationReaderConstants.REPOSITORY_AUTHENTICATION_DATA_PASSWORD);
            usedAuthentication = new BasicAuthentication(username, password);
        } else if (authenticationType.equals("ssh")) {
            String sshFilePath = hc.getString(XmlConfigurationReaderConstants.REPOSITORY_AUTHENTICATION_DATA_SSH_FILE_PATH);
            String username = hc.getString(XmlConfigurationReaderConstants.REPOSITORY_AUTHENTICATION_DATA_USERNAME);
            String password = hc.getString(XmlConfigurationReaderConstants.REPOSITORY_AUTHENTICATION_DATA_PASSWORD);
            String port = hc.getString(XmlConfigurationReaderConstants.REPOSITORY_AUTHENTICATION_DATA_PORT);
            usedAuthentication = new SshAuthentication(username, password, port, sshFilePath);
        }
        String versionControlSystem = hc.getString(XmlConfigurationReaderConstants.REPOSITORY_VCS);
        repo = new RepositoryDto(name, hc.getString(XmlConfigurationReaderConstants.REPOSITORY_URL), usedAuthentication, hc.getBoolean(XmlConfigurationReaderConstants.REPOSITORY_CODE_NAVIGATION_ENABLED), versionControlSystem, blacklistEntries, whitelistFileNames, repositoryGroups);

        LOG.info("Read repository: " + repo.getName());
        return repo;
    }

    private void loadJobs() throws InvalidConfigurationException {
        List<JobDto> jobs = new LinkedList<JobDto>();
        // read the configuration for the jobs from the config
        List<HierarchicalConfiguration> jobConfig = config.configurationsAt(XmlConfigurationReaderConstants.INDEX_JOB);

        for (HierarchicalConfiguration hc : jobConfig) {
            // reads job specific values and adds them to the JobDto
            JobDto job = new JobDto();
            String cronExpression;
            try {
                cronExpression = hc.getString(XmlConfigurationReaderConstants.JOB_CRON_EXPRESSION);
            } catch (NoSuchElementException ex) {
                // in case no interval was specified the job is set to execute only once
                cronExpression = "";
            }

            job.setCronExpression(cronExpression);

            String repositoryString = hc.getString(XmlConfigurationReaderConstants.JOB_REPOSITORY);
            // The list of repositories this job is associated with, each task specified in the configuration is created for each of
            // these repositories
            List<RepositoryDto> repositoriesForJob = getRepositoriesByNames(repositoryString);
            job.setRepositories(repositoriesForJob);
            boolean clearIndex = hc.getBoolean(XmlConfigurationReaderConstants.JOB_CLEAR, false);
            job.setClearIndex(clearIndex);
            jobs.add(job);
        }
        codesearchConfiguration.setJobs(jobs);
    }

    @Deprecated //users for indexer are not configured yet
    private void loadIndexerUsers() {
        List<IndexerUserDto> indexerUsers = new LinkedList<IndexerUserDto>();
        List<HierarchicalConfiguration> userConfig = config.configurationsAt(XmlConfigurationReaderConstants.INDEXER_USERS);
        for (HierarchicalConfiguration hc : userConfig) {
            IndexerUserDto userDto = new IndexerUserDto();
            userDto.setUserName(hc.getString(XmlConfigurationReaderConstants.USERNAME));
            userDto.setPassword((hc.getString(XmlConfigurationReaderConstants.PASSWORD)));
            indexerUsers.add(userDto);
        }
        codesearchConfiguration.setIndexerUsers(indexerUsers);
    }

    private void loadGlobalWhitelist() {
        List globalWhitelist = config.getList(XmlConfigurationReaderConstants.GLOBAL_WHITELIST);
        codesearchConfiguration.setGlobalWhitelist(globalWhitelist);
    }

    private void loadGlobalBlacklist() {
        List globalBlacklist = config.getList(XmlConfigurationReaderConstants.GLOBAL_BLACKLIST);
        codesearchConfiguration.setGlobalBlacklist(globalBlacklist);
    }

    /**
     * Takes a string of repository names and returns a {@link List} of the
     * corresponding {@link RepositoryDto} objects.
     *
     * @param repositoryNames The names of the repositories in a single string
     * separated by spaces.
     * @return the {@link List} of {@link RepositoryDto}s.
     */
    private List<RepositoryDto> getRepositoriesByNames(String repositoryNames) {
        if (repositoryNames == null) {
            return getRepositories();
        }
        List<RepositoryDto> repos = new LinkedList<RepositoryDto>();
        for (String name : repositoryNames.split(" ")) {
            for (RepositoryDto repositoryDto : codesearchConfiguration.getRepositories()) {
                if (repositoryDto.getName().equals(name)) {
                    repos.add(repositoryDto);
                }
            }
        }
        return repos;
    }
}
