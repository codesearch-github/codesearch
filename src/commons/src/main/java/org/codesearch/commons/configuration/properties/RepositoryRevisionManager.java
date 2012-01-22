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
package org.codesearch.commons.configuration.properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.log4j.Logger;
import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;

/**
 * Implementation that uses a property file to represent the currently indexed revision of a repository.
 * It uses the standard java Properties utility.
 *
 * @author Samuel Kogler
 */
@Singleton
public class RepositoryRevisionManager implements PropertiesManager {

    private static final Logger LOG = Logger.getLogger(RepositoryRevisionManager.class);
    /**
     * The used Property file.
     */
    private Properties properties = new Properties();
    private File repositoryStatusFile;
    private final String REPOSITORY_STATUS_FILENAME = "revisions.properties";

    /**
     * Creates a new instance of RepositoryRevisionManager
     *
     * @param configurationReader Used to read the index location for the properties file
     */
    @Inject
    public RepositoryRevisionManager(ConfigurationReader configurationReader) {
        try {
            repositoryStatusFile = new File(configurationReader.getIndexLocation(), REPOSITORY_STATUS_FILENAME);
            if (!repositoryStatusFile.exists()) {
                repositoryStatusFile.createNewFile();
            }
            properties.load(new FileReader(repositoryStatusFile));
        } catch (IOException ex) {
            LOG.error("Could not read properties file:\n" + ex);
        }
    }

    /**
     * Creates a new instance of RepositoryRevisionManager
     *
     * @param configFile The configuration file
     */
    public RepositoryRevisionManager(InputStream configFile) {
        try {
            properties.load(configFile);
        } catch (IOException ex) {
        }
    }

    /**
     * Gets a new value for the specified key
     *
     * @param key 
     * @return The value of the key, or {@link VersionControlPlugin.UNDEFINED_VERSION} if no value is found
     */
    @Override
    public synchronized String getValue(final String key) {
        return properties.getProperty(key, VersionControlPlugin.UNDEFINED_VERSION);
    }

    /**
     * Sets a new value for the specified key
     *
     * @param key
     * @param value
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Override
    public synchronized void setValue(final String key, final String value) throws FileNotFoundException, IOException {
        properties.setProperty(key, value);
        properties.store(new FileOutputStream(repositoryStatusFile), null);
    }
}
