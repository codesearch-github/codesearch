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
package org.codesearch.commons.configuration.properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Provides several methods to access property files.
 * It is a simple wrapper to the standard java Properties utility.
 * @author Stephan Stiboller
 */
public class PropertiesManager {

    private static final Logger LOG = Logger.getLogger(PropertiesManager.class);
    /** The used Property file. */
    private Properties properties = new Properties();
    /** The property file location. */
    private String propertyFileLocation;

    /**
     * Creates a new instance of PropertiesManager
     * @param the location of the property file
     */
    public PropertiesManager(final String propertyFileLocation) {
        try {
            this.propertyFileLocation = propertyFileLocation;
            File f = new File(propertyFileLocation);
            if (!f.exists()) {
                f.createNewFile();
            }
            properties.load(new FileReader(propertyFileLocation));
        } catch (IOException ex) {
            LOG.error("Could not read properties file:\n" + ex);
        }
    }

    /**
     * Creates a new instance of PropertiesManager
     * @param configFile The configuration file
     */
    public PropertiesManager(InputStream configFile) {
        try {
            properties.load(configFile);
        } catch (IOException ex) {
        }
    }

    /**
     * Gets a new value for the specified key
     * @param key
     * @param value
     */
    public String getPropertyFileValue(final String key) {
        return properties.getProperty(key, "0");
    }

    /**
     * Sets a new value for the specified key
     * @param key
     * @param value
     */
    public void setPropertyFileValue(final String key, final String value) throws FileNotFoundException, IOException {
        properties.setProperty(key, value);
        properties.store(new FileOutputStream(propertyFileLocation), null);
    }
}
