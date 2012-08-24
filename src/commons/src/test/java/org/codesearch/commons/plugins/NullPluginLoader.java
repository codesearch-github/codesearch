package org.codesearch.commons.plugins;

import java.util.LinkedList;
import java.util.List;

/**
 * Always returns null.
 * @author Samuel Kogler
 */
public class NullPluginLoader implements PluginLoader{

    @Override
    public <T extends Plugin> List<T> getAllPluginsOfClass(Class<T> clazz) {
        return new LinkedList<T>();
    }

    @Override
    public <T extends Plugin> T getPlugin(Class<T> clazz, String purpose) {
        return null;
    }

}
