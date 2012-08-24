package org.codesearch.commons.plugins;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.configuration.xml.NoOpConfigurationReader;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldPlugin;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author david
 */
public class PluginLoaderImplTest {
    private static PluginLoader pluginLoader;
    
    @BeforeClass
    public static void setup() throws PluginLoaderException {
        ConfigurationReader configurationReader = new NoOpConfigurationReader();
        pluginLoader = new PluginLoaderImpl(configurationReader);
    }

    /**
     * Tries to load the "core"-plugins, which are required for basic functionality
     */
    @Test
    public void getCorePlugins() throws PluginLoaderException {
        List<LuceneFieldPlugin> plugins = pluginLoader.getAllPluginsOfClass(LuceneFieldPlugin.class);
        assertTrue ("No core plugins found", plugins.size() == 4);
    }
}
