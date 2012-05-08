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
package org.codesearch.commons;

import org.apache.log4j.Logger;
import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.configuration.ConfigurationValidator;
import org.codesearch.commons.configuration.IndexCleaner;
import org.codesearch.commons.configuration.InvalidConfigurationException;
import org.codesearch.commons.configuration.properties.IndexStatusManager;
import org.codesearch.commons.configuration.properties.IndexStatusManagerPropertiesImpl;
import org.codesearch.commons.configuration.xml.XmlConfigurationReader;
import org.codesearch.commons.database.DBAccess;
import org.codesearch.commons.database.DBAccessImpl;
import org.codesearch.commons.plugins.PluginLoader;
import org.codesearch.commons.plugins.PluginLoaderImpl;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldPluginLoader;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldPluginLoaderImpl;
import org.codesearch.commons.utils.QuartzGuiceJobFactory;
import org.quartz.spi.JobFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * Guice module for commons. Wires everything together.
 *
 * @author Samuel Kogler
 */
public class CommonsGuiceModule extends AbstractModule {

    private final Logger LOG = Logger.getLogger(CommonsGuiceModule.class);

    @Override
    protected void configure() {
        try {
            bind(JobFactory.class).to(QuartzGuiceJobFactory.class).in(Singleton.class);
            ConfigurationReader configurationReader = new XmlConfigurationReader("codesearch_config.xml");
            PluginLoader pluginLoader = new PluginLoaderImpl(configurationReader);
            DBAccess dbaccess = new DBAccessImpl();
            IndexStatusManager indexStatusManager = new IndexStatusManagerPropertiesImpl(configurationReader);
            new ConfigurationValidator(configurationReader, pluginLoader);
            IndexCleaner indexCleaner = new IndexCleaner(indexStatusManager, configurationReader, dbaccess);
            indexCleaner.cleanIndex();
            
            bind(ConfigurationReader.class).toInstance(configurationReader);
            bind(PluginLoader.class).toInstance(pluginLoader);
            bind(IndexStatusManager.class).toInstance(indexStatusManager);
            bind(DBAccess.class).toInstance(dbaccess);
            
            bind(LuceneFieldPluginLoader.class).to(LuceneFieldPluginLoaderImpl.class).in(Singleton.class);
        } catch (InvalidConfigurationException ex) {
            LOG.error("Invalid configuration:", ex);
        }
    }
}
