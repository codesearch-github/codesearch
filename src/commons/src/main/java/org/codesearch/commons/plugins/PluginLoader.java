
package org.codesearch.commons.plugins;

import java.util.List;

/**
 *
 * @author Samuel Kogler
 */
public interface PluginLoader {

    <T extends Plugin> List<T> getMultiplePluginsForPurpose(final Class<T> clazz, final String purpose) throws PluginLoaderException;

    /**
     * returns a plugin for the given purpose. Searches through all loaded plugins and finds the one with the given
     * purpose.
     *
     * @param purpose the purpose of the plugin
     * @return the plugin matching the purpose
     * @throws Exception if no plugin was found with the purpose
     */
    @SuppressWarnings(value = "unchecked")
    <T extends Plugin> T getPlugin(final Class<T> clazz, final String purpose) throws PluginLoaderException;

}
