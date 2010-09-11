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

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.codesearch.commons.configreader.xml.dto.RepositoryDto;
import org.codesearch.commons.configreader.xml.dto.TaskDto;
import org.codesearch.commons.configreader.xml.dto.TaskDto.TaskType;

/**
 * PropertyManager is a class that provides several methods to access properties.
 * The properties are stored in a file called codesearch_config.xml, the path to this file can be
 * specified in the configpath.properties file which always has to be in the root folder of
 * the commons project.
 *
 * @author David Froehlich
 */
public class PropertyManager {

    /** The XMLConfiguration object that is used to read the properties from 
     * the xml-file, does not have to be instantiated actively hence it will
     * be checked and instantiated whenever used */
    private XMLConfiguration config;
    /** The path to the codesearch_config.xml file, will be read from the fielPath.properties file
     * or can be set manually to read a different config-file */
    private String configpath = "";

    /**
     * creates a new instance of PropertyManager
     */
    public PropertyManager() {
    }

    public List<TaskDto> getTasks() throws ConfigurationException {
        List<TaskDto> tasks = new LinkedList<TaskDto>();
        if(config == null){
            loadConfigReader();
        }
        List<HierarchicalConfiguration> taskConfig = config.configurationsAt("index_tasks.index_task");
        for(HierarchicalConfiguration hc : taskConfig){
            String repositoryString = hc.getString("repositories");
            List<String> repositories;
            if(repositoryString == null){
                repositories = null;
            } else{
                repositories = new LinkedList<String>();
                repositories.addAll(Arrays.asList(repositoryString.split(",")));
            }
            TaskType type = null;
            if(hc.getString("type").equals("index")){ //TODO replace with a more generic method
                type = TaskType.index;
            } else if(hc.getString("type").equals("clear")){
                type = TaskType.clear;
            }
            int interval = hc.getInt("interval");
            String[] timeParts = hc.getString("start").split("-");
            Calendar calc = new GregorianCalendar(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]),
                    Integer.parseInt(timeParts[2]), Integer.parseInt(timeParts[3]), Integer.parseInt(timeParts[4]));
            if(calc == null){
                throw new ConfigurationException("String for start date of task configuration is not correct");
            }
            tasks.add(new TaskDto(repositories, type, interval, calc));
        }
        return tasks;
    }

    /**
     * returns a list of RepositoryBeans containing information about the repositories
     * specified in the codesearch_config.xml file. Also checks if the XMLConfiguration is
     * instantiated and, if not, instantiates it with the configpath declared.
     * If no configpath is declared, the method will read the configpath from the configpath.properties
     * @return the list of all repositories
     * @throws ConfigurationException if either the readConfigPath() method throws
     * an exception or the codesearch_config.xml file is either not found or contains invalid data
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
     * returns the value of the given single-line-property from the codesearch_config.xml file
     * If the config reader object was not instanciated before, it will be created with the
     * configpath specified, if no configpath is specified it will read it from the configpath.properties file
     * @param key the key for the property
     * @return the value of the property
     * @throws ConfigurationException if either the configpath.properties file does not
     * exist or does not contain a valid value or if the config.xml file does not contain
     * a value for the given key
     */
    public String getSingleLinePropertyValue(String key) throws ConfigurationException {
        String value = "";
        if (config == null) {
            loadConfigReader();
        }
        value = config.getString(key);
        return value;
    }

    /**
     * returns the values of all single-line-properties form the codesearch_config.xml file that match the given key.
     * If the config reader object was not instanciated before, it will be created with the
     * configpath specified, if no configpath is specified it will read it from the configpath.properties file
     * @param key the key for the property
     * @return a list of the values of the properties
     * @throws ConfigurationException if either the configpath.properties file does not
     * exist or does not contain a valid value or if the config.xml file does not contain
     * a value for the given key
     */
    public List<String> getSingleLinePropertyValueList(String key) throws ConfigurationException {
        List<String> values = new LinkedList<String>();
            if (config == null) {
            loadConfigReader();
        }
        values = config.getList(key);
        return values;
    }

    /**
     * Reads the configpath.properties file which has to be in the root folder of
     * the commons project and specifies the filepath for the xml.config file
     * @throws ConfigurationException if the configpath.properties file is not
     * found or does not contain a valid filepath property
     */
    private void readConfigPath() throws ConfigurationException {
        Configuration configPathReader = new PropertiesConfiguration("configpath.properties");
        configpath = configPathReader.getString("filepath");
    }

    /**
     * loads the codesearch_config.xml file from the filepath specified in the configpath.properties file
     * instantiates the XMLConfiguration object through which the config can be read.
     * @throws ConfigurationException if either the configpath.properties file was not found
     * or contains invalid data, or if the config.xml file was not found.
     */
    private void loadConfigReader() throws ConfigurationException {
        if (configpath.equals("")) {
            readConfigPath();
        }
        config = new XMLConfiguration(configpath + File.separator + "codesearch_config.xml");
    }

    /**
     * returns the set configpath
     * @return the configpath
     */
    public String getConfigpath() {
        return configpath;
    }

    /**
     * sets the configpath to the given parameter
     * use this method before using any of the property retrieving methods to set a custom config file
     * @param configpath the path to the properties configuration
     */
    public void setConfigpath(String configpath) {
        this.configpath = configpath;
    }
}
