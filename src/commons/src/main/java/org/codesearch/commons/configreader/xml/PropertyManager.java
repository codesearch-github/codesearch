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
package org.codesearch.commons.configreader.xml;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.codesearch.commons.configreader.xml.dto.JobDto;
import org.codesearch.commons.configreader.xml.dto.RepositoryDto;
import org.codesearch.commons.configreader.xml.dto.TaskDto;
import org.codesearch.commons.configreader.xml.dto.TaskDto.TaskType;

/**
 * PropertyManager is a class that provides several methods to access properties.
 * By default, the properties are stored in a file in the classpath called codesearch_config.xml.
 *
 * @author David Froehlich
 * @author Samuel Kogler
 */
public class PropertyManager {

    /** The XMLConfiguration object that is used to read the properties from 
     * the XML-file, does not have to be instantiated actively, it will
     * be checked and instantiated whenever used */
    private XMLConfiguration config;
    /** The path to the configuration file. */
    private String configpath = "codesearch_config.xml";

    /**
     * creates a new instance of PropertyManager
     */
    public PropertyManager() {
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
        List<HierarchicalConfiguration> jobConfig = config.configurationsAt("index_jobs.index_job");
        for (HierarchicalConfiguration hc : jobConfig) {
            //reads job specific values and adds them to the JobDto
            JobDto job = new JobDto();
            int interval = hc.getInt("interval");
            //The start time is stored as a single string seperated by '-' e.g.: YYYY-MM-DD-HH-MM
            String[] timeParts = hc.getString("start").split("-");
            Calendar calc = new GregorianCalendar(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]),
                    Integer.parseInt(timeParts[2]), Integer.parseInt(timeParts[3]), Integer.parseInt(timeParts[4]));
            if (calc == null) {
                throw new ConfigurationException("String for start date of task configuration is not correct");
            }
            job.setInterval(interval);
            job.setStartDate(calc);
            //Read the tasks per job from the configuration
            List<SubnodeConfiguration> subConf = hc.configurationsAt("tasks.task");
            List<TaskDto> tasks = new LinkedList<TaskDto>();
            for(SubnodeConfiguration sc : subConf){
                TaskType type = null;
                if (sc.getString("type").equals("index")) { //TODO replace with a more generic method
                    type = TaskType.index;
                } else if (sc.getString("type").equals("clear")) {
                    type = TaskType.clear;
                }
                String repositoryString = sc.getString("repositories");
                List<String> repositories;
                if (repositoryString == null) {
                    repositories = null;
                } else {
                    repositories = new LinkedList<String>();
                    repositories.addAll(Arrays.asList(repositoryString.split(",")));
                }
                tasks.add(new TaskDto(repositories, type));
            }
            job.setTasks(tasks);
            jobs.add(job);
        }
        return jobs;
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
        List<HierarchicalConfiguration> repositoryConfigs = config.configurationsAt("repositories.repository");
        for (HierarchicalConfiguration repositoryConfig : repositoryConfigs) {
            RepositoryDto repositoryDto = new RepositoryDto();
            repositoryDto.setName(repositoryConfig.getString("name"));
            repositoryDto.setUrl(repositoryConfig.getString("url"));
            repositoryDto.setUsername(repositoryConfig.getString("username"));
            repositoryDto.setPassword(repositoryConfig.getString("password"));
            repositoryDto.setIndexingEnabled(repositoryConfig.getBoolean("indexingEnabled"));
            repositoryDto.setCodeNavigationEnabled(repositoryConfig.getBoolean("codeNavigationEnabled"));
            repositories.add(repositoryDto);
        }
        return repositories;
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
}
