/**
 * Copyright 2010 David Froehlich   <david.froehlich@businesssoftware.at>,
 *                Samuel Kogler     <samuel.kogler@gmail.com>,
 *                Stephan Stiboller <stistc06@htlkaindorf.at>
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Codesearch.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.codesearch.commons.plugins;

import java.util.Iterator;
import com.google.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.configuration.xml.XmlConfigurationReaderConstants;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;
import org.codesearch.commons.plugins.vcs.VersionControlPluginException;

/**
 * A class that provides access to dynamically loaded plugins.
 *
 * @author Stiboller Stephan
 * @author David Froehlich
 * @author Samuel Kogler
 */
public class PluginLoaderImpl implements PluginLoader {

    private ServiceLoader<Plugin> serviceLoader = ServiceLoader.load(Plugin.class);
    private ConfigurationReader configurationReader;

    @Inject
    public PluginLoaderImpl(ConfigurationReader configurationReader) {
        this.configurationReader = configurationReader;
    }

    /**
     * returns a plugin for the given purpose. Searches through all loaded plugins and finds the one with the given
     * purpose.
     *
     * @param purpose the purpose of the plugin
     * @return the plugin matching the purpose
     * @throws Exception if no plugin was found with the purpose
     */
    @SuppressWarnings("unchecked")
    @Override
    public synchronized <T extends Plugin> T getPlugin(final Class<T> clazz, final String purpose)
            throws PluginLoaderException {
        Plugin validPlugin = null;
        Iterator iter = serviceLoader.iterator();
        
        for (Plugin plugin : serviceLoader) {
            try {
                String[] purposes = plugin.getPurposes().split(" ");
                for (String s : purposes) {
                    if (s.equalsIgnoreCase(purpose) && clazz.isInstance(plugin)) {
                        if (validPlugin == null) {
                            validPlugin = plugin;
                        } else {
                            throw new PluginLoaderException("Multiple plugins found for purpose " + purpose + " and class " + clazz + " which only supports a single plugin");
                        }
                    }
                }
            } catch (ServiceConfigurationError ex) {
                System.out.println(ex.toString());
            }
        }
        //FIXME: very hackish, but I could not think of a better way ATM
        if (validPlugin != null) {
            if (validPlugin instanceof VersionControlPlugin) {
                VersionControlPlugin vcsPlugin = (VersionControlPlugin) validPlugin;
                try {
                    vcsPlugin.setCacheDirectory(configurationReader.getValue(XmlConfigurationReaderConstants.VERSION_CONTROL_CACHE_DIR));
                } catch (VersionControlPluginException ex) {
                    throw new PluginLoaderException("VersionControlPlugin failed to load because an invalid cache directory was specified: " + ex);
                }
            }

            return (T) validPlugin;
        }
        throw new PluginLoaderException("No Plugin found for purpose: " + purpose + " and Class: " + clazz);
    }

    @Override
    public synchronized <T extends Plugin> List<T> getMultiplePluginsForPurpose(final Class<T> clazz, final String purpose) throws PluginLoaderException {
        List<T> validPlugins = new LinkedList<T>();
        for (Plugin plugin : serviceLoader) {
            String[] purposes = plugin.getPurposes().split(" ");
            for (String s : purposes) {
                if (s.equalsIgnoreCase(purpose)) {
                    validPlugins.add((T) plugin);
                }
            }
        }
        if (validPlugins.isEmpty()) {
            throw new PluginLoaderException("No Plugin found for purpose: " + purpose + " and Class: " + clazz);
        }
        return validPlugins;
    }
}
