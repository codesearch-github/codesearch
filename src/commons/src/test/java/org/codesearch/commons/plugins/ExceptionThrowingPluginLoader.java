package org.codesearch.commons.plugins;

import java.util.List;

/**
 *
 * @author Samuel Kogler
 */
public class ExceptionThrowingPluginLoader implements PluginLoader{

    @Override
    public <T extends Plugin> List<T> getMultiplePluginsForPurpose(Class<T> clazz, String purpose) throws PluginLoaderException {
        throw new PluginLoaderException();
    }

    @Override
    public <T extends Plugin> T getPlugin(Class<T> clazz, String purpose) throws PluginLoaderException {
        throw new PluginLoaderException();
    }

}
