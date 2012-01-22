package org.codesearch.commons.plugins;

import java.util.List;
import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.configuration.xml.NoOpConfigurationReader;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldPlugin;
import org.junit.Test;

/**
 *
 * @author david
 */
public class PluginLoaderImplTest {
    private PluginLoader pluginLoader;
    
    public PluginLoaderImplTest() {
        ConfigurationReader configurationReader = new NoOpConfigurationReader();
        this.pluginLoader = new PluginLoaderImpl(configurationReader);
    }

    /**
     * Tries to load the "core"-plugins, which are required for basic functionality
     */
    @Test
    public void getCorePlugins() throws PluginLoaderException {
        List<LuceneFieldPlugin> plugins = pluginLoader.getMultiplePluginsForPurpose(LuceneFieldPlugin.class, "lucene_field_plugin");
        //FIXME check that all required plugins are actually found
        assert (plugins.size() > 0);
    }
}
