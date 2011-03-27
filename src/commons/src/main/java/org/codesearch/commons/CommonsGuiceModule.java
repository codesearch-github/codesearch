package org.codesearch.commons;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.configuration.xml.XmlConfigurationReader;
import org.codesearch.commons.database.ConnectionPool;
import org.codesearch.commons.database.ConnectionPoolImpl;
import org.codesearch.commons.database.DBAccess;
import org.codesearch.commons.database.DBAccessImpl;
import org.codesearch.commons.plugins.PluginLoader;
import org.codesearch.commons.plugins.PluginLoaderImpl;

/**
 * Guice module for commons. Wires everything together.
 * @author Samuel Kogler
 */
public class CommonsGuiceModule extends AbstractModule{

    @Override
    protected void configure() {
        bind(ConfigurationReader.class).to(XmlConfigurationReader.class).in(Singleton.class);
        bind(ConnectionPool.class).to(ConnectionPoolImpl.class).in(Singleton.class);
        bind(DBAccess.class).to(DBAccessImpl.class).in(Singleton.class);
        bind(PluginLoader.class).to(PluginLoaderImpl.class).in(Singleton.class);
        bind(String.class).annotatedWith(Names.named("configpath")).toInstance("codesearch_config.xml");
    }

}
