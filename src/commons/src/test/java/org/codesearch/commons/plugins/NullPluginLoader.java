package org.codesearch.commons.plugins;

import java.util.List;

/**
 * Always returns null.
 * @author Samuel Kogler
 */
public class NullPluginLoader implements PluginLoader{

    @Override
    public <T extends Plugin> List<T> getMultiplePluginsForPurpose(Class<T> clazz, String purpose) throws PluginLoaderException {
        return null;
    }

    @Override
    public <T extends Plugin> T getPlugin(Class<T> clazz, String purpose) throws PluginLoaderException {
        return null;
    }

}
