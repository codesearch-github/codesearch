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

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Stiboller Stephan
 * @author David Froehlich
 * @author Samuel Kogler
 */
public class PluginLoader {

    /** The list of all plugins that have been loaded. */
    @Autowired
    private List<Plugin> loadedPlugins;

    /**
     * returns a plugin for the given purpose.
     * Searches through all loaded plugins and finds the one with the given purpose.
     * @param purpose the purpose of the plugin
     * @return the plugin matching the purpose
     * @throws Exception if no plugin was found with the purpose
     */
    public <T extends Plugin> T getPlugin(final Class clazz, final String purpose) throws PluginLoaderException {
        for (Plugin plugin : loadedPlugins) {
            if (plugin.getPurpose().equals(purpose)) {
                if (clazz.isInstance(plugin)) {
                    return (T) plugin;
                }
            }
        }
        throw new PluginLoaderException("No Plugin found for purpose: " + purpose + " and Class: " + clazz);
    }

    public List<Plugin> getLoadedPlugins() {
        return loadedPlugins;
    }

    public void setLoadedPlugins(List<Plugin> loadedPlugins) {
        this.loadedPlugins = loadedPlugins;
    }
}
