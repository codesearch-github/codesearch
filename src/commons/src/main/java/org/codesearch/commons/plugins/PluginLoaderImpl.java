/**
 * Copyright 2010 David Froehlich <david.froehlich@businesssoftware.at>,
 * Samuel Kogler <samuel.kogler@gmail.com>,
 * Stephan Stiboller <stistc06@htlkaindorf.at>
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Codesearch. If not, see <http://www.gnu.org/licenses/>.
 */
package org.codesearch.commons.plugins;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.log4j.Logger;
import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;
import org.codesearch.commons.plugins.vcs.VersionControlPluginException;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;

public class PluginLoaderImpl implements PluginLoader {

    private static final Logger LOG = Logger.getLogger(PluginLoaderImpl.class);
    private ServiceLoader<Plugin> serviceLoader;
    private Multimap<Class<? extends Plugin>, Plugin> loadedPlugins = LinkedListMultimap.create();
    private Multimap<Class<? extends Plugin>, String> basePluginPurposeMap = LinkedListMultimap.create();

    @Inject
    public PluginLoaderImpl(ConfigurationReader configurationReader) throws PluginLoaderException {
        serviceLoader = ServiceLoader.load(Plugin.class);
        for (Plugin plugin : serviceLoader) {
            Class<? extends Plugin> basePlugin = findPluginBase(plugin.getClass());
            if (basePlugin != null) {
                // Validate that there are no duplicate purposes per plugin type
                Collection<String> alreadyLoadedPurposes = basePluginPurposeMap.get(basePlugin);
                for (String purpose : plugin.getPurposes().split(" ")) {
                    if (alreadyLoadedPurposes.contains(purpose.toLowerCase())) {
                        throw new PluginLoaderException("Multiple plugins that implement " + basePlugin.getCanonicalName()
                                + " available for purpose: " + purpose);
                    } else {
                        alreadyLoadedPurposes.add(purpose);
                    }
                }

                loadedPlugins.put(basePlugin, plugin);

                if (plugin instanceof VersionControlPlugin) {
                    VersionControlPlugin vcsPlugin = (VersionControlPlugin) plugin;
                    try {
                        vcsPlugin.setCacheDirectory(configurationReader.getCacheDirectory().getAbsolutePath());
                    } catch (VersionControlPluginException ex) {
                        throw new PluginLoaderException(
                                "VersionControlPlugin failed to load because an invalid cache directory was specified: " + ex);
                    }
                }

            } else {
                LOG.warn("Invalid plugin class found: " + plugin.getClass().getCanonicalName());
            }
        }
    }

    @Override
    public synchronized <T extends Plugin> T getPlugin(final Class<T> clazz, final String purpose) {
        if (!loadedPlugins.containsKey(clazz)) {
            LOG.warn("Specified plugin base class " + clazz.getCanonicalName() + " has no known implementations");
            return null;
        }

        for (Plugin plugin : loadedPlugins.get(clazz)) {
            String[] purposes = plugin.getPurposes().split(" ");
            for (String s : purposes) {
                if (s.equalsIgnoreCase(purpose)) {
                    return clazz.cast(plugin);
                }
            }
        }
        return null;
    }

    @Override
    public synchronized <T extends Plugin> List<T> getAllPluginsOfClass(final Class<T> clazz) {
        List<T> implementations = new LinkedList<T>();
        for (Plugin plugin : loadedPlugins.get(clazz)) {
            implementations.add(clazz.cast(plugin));
        }
        return implementations;
    }

    /**
     * Finds the base plugin for a given class
     * 
     * @param Concrete plugin class
     * @return
     */
    @SuppressWarnings("unchecked")
    private Class<? extends Plugin> findPluginBase(Class<? extends Plugin> class1) {
        if (class1.getSuperclass() == null || class1.getSuperclass() == java.lang.Object.class) {
            for (Class<?> iface : class1.getInterfaces()) {
                if (Plugin.class.isAssignableFrom(iface)) {
                    if (iface.isAnnotationPresent(PluginBase.class)) {
                        return (Class<? extends Plugin>) iface;
                    }
                }
            }
            // Not a valid plugin class
            return null;
        } else {
            return findPluginBase((Class<? extends Plugin>) class1.getSuperclass());
        }
    }
}
