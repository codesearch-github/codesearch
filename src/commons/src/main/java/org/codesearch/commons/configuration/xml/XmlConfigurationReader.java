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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.codesearch.commons.configuration.xml.dto.JobDto;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.configuration.xml.dto.TaskDto;
import org.codesearch.commons.configuration.xml.dto.TaskDto.TaskType;

/**
 * XmlConfigurationReader is a class that provides several methods to access properties.
 * By default, the properties are stored in a file in the classpath called codesearch_config.xml.
 * @author Stephan Stiboller
 * @author David Froehlich
 * @author Samuel Kogler
 */
public class XmlConfigurationReader {

    /** The XMLConfiguration object that is used to read the properties from the XML-file*/
    private XMLConfiguration config;
    /** The path to the configuration file. */
    private String configpath = "codesearch_config.xml";

    /**
     * creates a new instance of XmlConfigurationReader
     */
    public XmlConfigurationReader() {
    }

    /**
     * Retrieves a list of all indexer_jobs from the configuration and returns it as a list of JobDto
     * @return The list of JobDtos
     * @throws ConfigurationException If the configuration could not be read or the keys were not found
     */
    public List<JobDto> getJobs() throws ConfigurationException {
        List<JobDto> jobs = new LinkedList<JobDto>();
        if (config == null) {
            loadConfigReader();
        }
        //read the configuration for the jobs from the config
        List<HierarchicalConfiguration> jobConfig = config.configurationsAt(XmlConfigurationReaderConstants.INDEX_JOB);
        for (HierarchicalConfiguration hc : jobConfig) {
            //reads job specific values and adds them to the JobDto
            try {
                JobDto job = new JobDto();
                int interval;
                try {
                    interval = hc.getInt(XmlConfigurationReaderConstants.JOB_INTERVAL);
                } catch (NoSuchElementException ex) {
                    //in case no interval was specified the job is set to execute only once
                    interval = 0;
                }
                //The start time is stored as a single string seperated by '-' e.g.: YYYY-MM-DD-HH-MM
                String timeString = hc.getString(XmlConfigurationReaderConstants.JOB_START_DATE);
                Calendar calc;
                if (timeString == null) {
                    calc = GregorianCalendar.getInstance();
                } else {

                    String[] timeParts = timeString.split("-");
                    calc = new GregorianCalendar(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]) - 1,
                            Integer.parseInt(timeParts[2]), Integer.parseInt(timeParts[3]), Integer.parseInt(timeParts[4]));
                }
                job.setInterval(interval);
                job.setStartDate(calc);

                String repositoryString = hc.getString(XmlConfigurationReaderConstants.JOB_REPOSITORY);
                //The list of repositories this job is associated with, each task specified in the configuration is created for each of these repositories
                List<RepositoryDto> repositoriesForJob = getRepositoryDtosForString(repositoryString);
                //Read the tasks per job from the configuration
                List<SubnodeConfiguration> subConf = hc.configurationsAt(XmlConfigurationReaderConstants.TASK_LIST);
                List<TaskDto> tasks = new LinkedList<TaskDto>();

                for (SubnodeConfiguration sc : subConf) {
                    TaskType type = null;
                    if (sc.getString(XmlConfigurationReaderConstants.TASK_TYPE).equals("index")) { //TODO replace with a more generic method
                        type = TaskType.index;
                    } else if (sc.getString(XmlConfigurationReaderConstants.TASK_TYPE).equals("clear")) {
                        type = TaskType.clear;
                    }
                    if (type == TaskType.clear && repositoryString == null) {
                        tasks.add(new TaskDto(null, type));
                    } else {
                        for (RepositoryDto repository : repositoriesForJob) {
                            tasks.add(new TaskDto(repository, type));
                        }
                    }
                }
                job.setTasks(tasks);
                jobs.add(job);
            } catch (NullPointerException ex) {
                return jobs;
            }
        }
        return jobs;
    }

    /**
     * returns all repositories which have names that are contained in the parameter
     * @param repositoryString the list of repositories, split by spaces
     * @return the list of repositories as dtos
     * @throws ConfigurationException if the configuration could not be loaded
     */
    private List<RepositoryDto> getRepositoryDtosForString(String repositoryString) throws ConfigurationException {
        if (config == null) {
            loadConfigReader();
        }
        if (repositoryString == null) {
            return getRepositories();
        }
        List<RepositoryDto> repos = new LinkedList<RepositoryDto>();
        for (String s : repositoryString.split(" ")) {
            repos.add(getRepositoryByName(s));
        }
        return repos;
    }

    /**
     * Returns the repository dto with the given name
     * @param name the name of the repository
     * @return the dto of the repository or null if none was found
     * @throws ConfigurationException if the configuration could not be loaded
     */
    public RepositoryDto getRepositoryByName(String name) throws ConfigurationException {
        if (config == null) {
            loadConfigReader();
        }
        RepositoryDto repo = null;
        List<HierarchicalConfiguration> repositories = config.configurationsAt(XmlConfigurationReaderConstants.REPOSITORY_LIST);
        for (HierarchicalConfiguration hc : repositories) {
            if (hc.getString(XmlConfigurationReaderConstants.REPOSITORY_NAME).equals(name)) {
                List<String> ignoredFileNames = hc.getList(XmlConfigurationReaderConstants.REPOSITORY_BLACKLIST);
                if (ignoredFileNames == null) {
                    ignoredFileNames = new LinkedList<String>();
                }
                List<String> repositoryGroups = hc.getList(XmlConfigurationReaderConstants.REPOSITORY_BLACKLIST);
                if (repositoryGroups == null) {
                    repositoryGroups = new LinkedList<String>();
                }
                ignoredFileNames.addAll(getGloballyIgnoredFileNames());
                repo = new RepositoryDto(name, hc.getString(XmlConfigurationReaderConstants.REPOSITORY_URL),
                        hc.getString(XmlConfigurationReaderConstants.REPOSITORY_USERNAME),
                        hc.getString(XmlConfigurationReaderConstants.REPOSITORY_PASSWORD),
                        hc.getBoolean(XmlConfigurationReaderConstants.REPOSITORY_CODE_NAVIGATION_ENABLED),
                        hc.getString(XmlConfigurationReaderConstants.REPOSITORY_VCS),
                        ignoredFileNames,
                        repositoryGroups);
            }
        }
        return repo;
    }

    private List<String> getGloballyIgnoredFileNames() throws ConfigurationException {
        if (config == null) {
            loadConfigReader();
        }
        return config.getList(XmlConfigurationReaderConstants.GLOBAL_BLACKLIST);
    }

    /**
     * Returns a list of repositories defined in the configuration.
     * Checks if the XMLConfiguration is instantiated and, if not, instantiates it.
     * @return the list of all repositories
     * @throws ConfigurationException If the config file is either not found or contains invalid data.
     */
    public List<RepositoryDto> getRepositories() throws ConfigurationException {
        if (config == null) {
            loadConfigReader();
        }
        List<RepositoryDto> repositories = new LinkedList<RepositoryDto>();
        List<HierarchicalConfiguration> repositoryConfigs = config.configurationsAt(XmlConfigurationReaderConstants.REPOSITORY_LIST);
        for (HierarchicalConfiguration repositoryConfig : repositoryConfigs) {
            RepositoryDto repositoryDto = new RepositoryDto();
            repositoryDto.setVersionControlSystem(repositoryConfig.getString(XmlConfigurationReaderConstants.REPOSITORY_VCS));
            repositoryDto.setName(repositoryConfig.getString(XmlConfigurationReaderConstants.REPOSITORY_NAME));
            repositoryDto.setUrl(repositoryConfig.getString(XmlConfigurationReaderConstants.REPOSITORY_URL));
            repositoryDto.setUsername(repositoryConfig.getString(XmlConfigurationReaderConstants.REPOSITORY_USERNAME));
            repositoryDto.setPassword(repositoryConfig.getString(XmlConfigurationReaderConstants.REPOSITORY_PASSWORD));
            repositoryDto.setCodeNavigationEnabled(repositoryConfig.getBoolean(XmlConfigurationReaderConstants.REPOSITORY_CODE_NAVIGATION_ENABLED));
            List<String> ignoredFileNames = repositoryConfig.getList(XmlConfigurationReaderConstants.REPOSITORY_BLACKLIST);
            if (ignoredFileNames == null) {
                ignoredFileNames = new LinkedList<String>();
            }
            ignoredFileNames.addAll(this.getGloballyIgnoredFileNames());
            repositoryDto.setIgnoredFileNames(ignoredFileNames);
            List<String> repositoryGroups = repositoryConfig.getList(XmlConfigurationReaderConstants.REPOSITORY_GROUPS);
            if (repositoryGroups == null) {
                repositoryGroups = new LinkedList<String>();
            }
            repositoryDto.setRepositoryGroups(repositoryGroups);
            repositories.add(repositoryDto);
        }
        return repositories;
    }

    /**
     * Retrieves all existing Repository groups 
     * @return list of all repo groups
     * @throws ConfigurationException
     */
    public List<String> getRepositoryGroups() throws ConfigurationException {
        if (config == null) {
            loadConfigReader();
        }
        List<String> groups = new LinkedList<String>();
        groups = Arrays.asList(config.getString(XmlConfigurationReaderConstants.REPOSITORY_GROUP_LIST).split(" "));
        return groups;
    }

    /**
     * Returns the value of the given single-line property from the configuration file.
     * @param key the key of the property
     * @return the value of the property
     * @throws ConfigurationException If the configuration file does not contain a value for the given key.
     */
    public String getSingleLinePropertyValue(final String key) throws ConfigurationException {
        if (config == null) {
            loadConfigReader();
        }
        return config.getString(key);
    }

    /**
     * Returns the values of all single-line properties that match the given key from the configuration file.
     * @param key the key for the property
     * @return a list of the values of the properties
     * @throws ConfigurationException If the configuration file does not contain a value for the given key
     */
    public List<String> getSingleLinePropertyValueList(final String key) throws ConfigurationException {
        List<String> values = new LinkedList<String>();
        if (config == null) {
            loadConfigReader();
        }
        values = config.getList(key);
        return values;
    }

    public List<String> getRepositoriesForGroup(String groupName) throws ConfigurationException {
        List<String> repos = new LinkedList<String>();
        for(RepositoryDto repo : getRepositories()){
            if(repo.getRepositoryGroups().contains(groupName)){
                repos.add(repo.getName());
            }
        }
        return repos;
    }

    /**
     * Loads the configuration file from the default or specified path.
     * @throws ConfigurationException If the configuration file was not found.
     */
    private void loadConfigReader() throws ConfigurationException {
        config = new XMLConfiguration(configpath);
    }

    public String getConfigpath() {
        return configpath;
    }

    public void setConfigpath(final String configpath) {
        this.configpath = configpath;
    }

    public void clearConfigurationReader() {
        this.configpath = null;
        this.config = null;
    }
}
