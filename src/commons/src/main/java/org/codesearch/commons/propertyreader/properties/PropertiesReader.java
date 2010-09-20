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
package org.codesearch.commons.propertyreader.properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * PropertiesReader is a class that provides several methods to access properties.
 * By default, the properties are stored in a file in the classpath called codesearch_config.xml.
 * @author Stephan Stiboller
 */
public class PropertiesReader {

    /** The internally used Property file */
    private Properties properties = new Properties();
    /** The repository property file */
    private String repositoryPropertyFile = "repository.properties";

    /**
     * Creates a new instance of PropertyManager
     * @param the location oft he property file
     */
    public PropertiesReader(String repositoryPropertyFile) {
        this.repositoryPropertyFile = repositoryPropertyFile;
    }

    /**
     * Creates a new instance of PropertyManager
     */
    public PropertiesReader() {
    }


    /**
     * Gets a new value for the specified key
     * @param key
     * @param value
     */
    public String getPropertyFileValue(final String key) throws FileNotFoundException, IOException {
        properties.load(new FileInputStream(getRepositoryPropertyFile()));
        return properties.getProperty(key);
    }

    /**
     * Sets a new value for the specified key
     * @param key
     * @param value
     */
    public void setPropertyFileValue(final String key, final String value) throws FileNotFoundException, IOException {
        properties.load(new FileInputStream(getRepositoryPropertyFile()));
        properties.setProperty(key, value);
        properties.store(new FileOutputStream(getRepositoryPropertyFile()), null);
    }

    /**
     * @return the repositoryPropertyFile
     */
    public String getRepositoryPropertyFile() {
        return repositoryPropertyFile;
    }

    /**
     * @param repositoryPropertyFile the repositoryPropertyFile to set
     */
    public void setRepositoryPropertyFile(String repositoryPropertyFile) {
        this.repositoryPropertyFile = repositoryPropertyFile;
    }
}
