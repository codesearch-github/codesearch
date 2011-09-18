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
package org.codesearch.commons.configuration.xml;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.configuration.xml.dto.IndexerUserDto;
import org.codesearch.commons.configuration.xml.dto.JobDto;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.plugins.vcs.AuthenticationType;
import org.codesearch.commons.plugins.vcs.BasicAuthentication;
import org.codesearch.commons.plugins.vcs.NoAuthentication;
import org.codesearch.commons.plugins.vcs.SshAuthentication;
import org.codesearch.commons.validator.ValidationException;
import org.codesearch.commons.validator.XmlConfigurationValidator;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * XmlConfigurationReader is a class that provides several methods to access properties.
 * By default, the properties are stored in a file in the classpath called codesearch_config.xml.
 * @author Stephan Stiboller
 * @author David Froehlich
 * @author Samuel Kogler
 */
public class XmlConfigurationReader implements ConfigurationReader {

    private static final Logger LOG = Logger.getLogger(XmlConfigurationReader.class);
    /** The XMLConfiguration object that is used to read the properties from the XML-file*/
    private XMLConfiguration config;
    /** The path to the configuration file. */
    private String configPath = "codesearch_config.xml";

    /**
     * creates a new instance of XmlConfigurationReader
     * @param configPath the classpath of the config file
     */
    @Inject
    public XmlConfigurationReader(@Named("configpath") String configPath) {
        if (StringUtils.isNotEmpty(configPath)) {
            this.configPath = configPath;
        }
        LOG.debug("Reading config file: " + this.configPath);
        try {
            config = new XMLConfiguration(this.configPath);
            config.load();
        } catch (ConfigurationException ex) {
            LOG.error("Configuration file could not be read:\n" + ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public synchronized List<IndexerUserDto> getIndexerUsers() {
        List<IndexerUserDto> users = new LinkedList<IndexerUserDto>();
        List<HierarchicalConfiguration> userConfig = config.configurationsAt(XmlConfigurationReaderConstants.INDEXER_USERS);
        for (HierarchicalConfiguration hc : userConfig) {
            IndexerUserDto userDto = new IndexerUserDto();
            userDto.setUserName(hc.getString(XmlConfigurationReaderConstants.USERNAME));
            userDto.setPassword((hc.getString(XmlConfigurationReaderConstants.PASSWORD)));
            users.add(userDto);
        }
        return users;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized List<JobDto> getJobs() {
        List<JobDto> jobs = new LinkedList<JobDto>();
        //read the configuration for the jobs from the config
        List<HierarchicalConfiguration> jobConfig = config.configurationsAt(XmlConfigurationReaderConstants.INDEX_JOB);
        for (HierarchicalConfiguration hc : jobConfig) {
            //reads job specific values and adds them to the JobDto
            try {
                JobDto job = new JobDto();
                String cronExpression;
                try {
                    cronExpression = hc.getString(XmlConfigurationReaderConstants.JOB_CRON_EXPRESSION);
                } catch (NoSuchElementException ex) {
                    //in case no interval was specified the job is set to execute only once
                    cronExpression = "";
                }

                job.setCronExpression(cronExpression);

                String repositoryString = hc.getString(XmlConfigurationReaderConstants.JOB_REPOSITORY);
                //The list of repositories this job is associated with, each task specified in the configuration is created for each of these repositories
                List<RepositoryDto> repositoriesForJob = getRepositoryDtosForString(repositoryString);
                job.setRepositories(repositoriesForJob);
                boolean clearIndex = hc.getBoolean(XmlConfigurationReaderConstants.JOB_CLEAR);
                job.setClearIndex(clearIndex);
                jobs.add(job);
            } catch (NullPointerException ex) {
                return jobs;
            }
        }
        return jobs;
    }

    /** {@inheritDoc} */
    private synchronized List<RepositoryDto> getRepositoryDtosForString(String repositoryString) {
        if (repositoryString == null) {
            return getRepositories();
        }
        List<RepositoryDto> repos = new LinkedList<RepositoryDto>();
        for (String s : repositoryString.split(" ")) {
            repos.add(getRepositoryByName(s));
        }
        return repos;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized RepositoryDto getRepositoryByName(String name) {
        RepositoryDto repo = null;
        List<HierarchicalConfiguration> repositories = config.configurationsAt(XmlConfigurationReaderConstants.REPOSITORY_LIST);
        for (HierarchicalConfiguration hc : repositories) {
            if (hc.getString(XmlConfigurationReaderConstants.REPOSITORY_NAME).equals(name)) {
                repo = getRepositoryFromConfig(hc);
            }
        }
        return repo;
    }

    /**
     * retrieves all required data about the given repository from the configuration via the HierarchicalConfiguration and returns it as a RepositoryDto
     */
    private synchronized RepositoryDto getRepositoryFromConfig(HierarchicalConfiguration hc) {
        RepositoryDto repo = new RepositoryDto();
        //retrieve the repository blacklisted filenames and add all global filenames
        String name = hc.getString(XmlConfigurationReaderConstants.REPOSITORY_NAME);
        List<String> blacklistEntries = hc.getList(XmlConfigurationReaderConstants.REPOSITORY_BLACKLIST);
        if (blacklistEntries == null) {
            blacklistEntries = new LinkedList<String>();
        }
        blacklistEntries.addAll(getGlobalBlacklistEntries());

        //retrieve the repository whitelisted filenames and add all global filenames
        List<String> whitelistFileNames = hc.getList(XmlConfigurationReaderConstants.REPOSITORY_WHITELIST_FILENAMES);
        if (whitelistFileNames == null) {
            whitelistFileNames = new LinkedList<String>();
        }
        whitelistFileNames.addAll(getGlobalWhitelistEntries());

        List<String> repositoryGroups = hc.getList(XmlConfigurationReaderConstants.REPOSITORY_GROUPS);
        if (repositoryGroups == null) {
            repositoryGroups = new LinkedList<String>();
        }

        //retrieve the used authentication system and fill it with the required data
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
            usedAuthentication = new SshAuthentication(sshFilePath);
        }

        repo = new RepositoryDto(name, hc.getString(XmlConfigurationReaderConstants.REPOSITORY_URL),
                usedAuthentication,
                hc.getBoolean(XmlConfigurationReaderConstants.REPOSITORY_CODE_NAVIGATION_ENABLED),
                hc.getString(XmlConfigurationReaderConstants.REPOSITORY_VCS),
                blacklistEntries,
                whitelistFileNames,
                repositoryGroups);
        LOG.info("reading repository from configuration: " + repo.toString());
        return repo;
    }

    /**
     * returns a list of all globally whitelisted filenames, so every filename has to match at least one of the whitelist names (only if the whitelist is not empty)
     */
    public synchronized List<String> getGlobalWhitelistEntries() {
        List<String> whitelist = config.getList(XmlConfigurationReaderConstants.GLOBAL_WHITELIST);
        if (whitelist == null) {
            whitelist = new LinkedList<String>();
        }
        return whitelist;
    }

    /**
     * returns a list of all file name patterns that are listed to be ignored on the entire system
     */
    private synchronized List<String> getGlobalBlacklistEntries() {
        List<String> blacklist = config.getList(XmlConfigurationReaderConstants.GLOBAL_BLACKLIST);
        if (blacklist == null) {
            blacklist = new LinkedList<String>();
        }
        return blacklist;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized List<RepositoryDto> getRepositories() {
        List<RepositoryDto> repositories = new LinkedList<RepositoryDto>();
        List<HierarchicalConfiguration> repositoryConfigs = config.configurationsAt(XmlConfigurationReaderConstants.REPOSITORY_LIST);
        for (HierarchicalConfiguration repositoryConfig : repositoryConfigs) {
            repositories.add(getRepositoryFromConfig(repositoryConfig));
        }
        return repositories;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized List<String> getRepositoryGroups() {
        List<String> groups = new LinkedList<String>();
        groups = Arrays.asList(config.getString(XmlConfigurationReaderConstants.REPOSITORY_GROUP_LIST).split(" "));
        return groups;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized String getValue(final String key) {
        return config.getString(key);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized List<String> getValueList(final String key) {
        List<String> values = new LinkedList<String>();
        values = config.getList(key);
        return values;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized List<String> getRepositoriesForGroup(String groupName) {
        List<String> repos = new LinkedList<String>();
        for (RepositoryDto repo : getRepositories()) {
            if (repo.getRepositoryGroups().contains(groupName)) {
                repos.add(repo.getName());
            }
        }
        return repos;
    }

    /** {@inheritDoc} */
    @Override
    public void validateConfiguration() throws ValidationException {
        new XmlConfigurationValidator(this.config).validateConfiguration();
        
    }
}
