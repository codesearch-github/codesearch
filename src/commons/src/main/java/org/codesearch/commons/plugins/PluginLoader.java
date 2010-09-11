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
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 *
 * @author David Froehlich
 */
public final class PluginLoader {

    /** the list of all plugins that have been loaded at the constructor call */
    private Map<String, Plugin> loadedPlugins;

    /**
     * Creates a new instance of plugin loader that loads all classes that implement the given classes type
     * @param the super class / interface of which the subclasses are loaded
     */
    public PluginLoader(Class clazz) {
        ApplicationContext context = new FileSystemXmlApplicationContext("spring-plugin-config.xml"); //move to a better 
        loadedPlugins = context.getBeansOfType(clazz); //replace with generic
    }

    /**
     * returns a plugin for the given purpose.
     * Searches through all plugins originally loaded at the constructor call and compares the purpose of each plugin with the given parameter.
     * Note that if there are multiple plugins with a fitting purpose it will return the first in the map (which is ordered alphabetically).
     * @param type the purpose for the plugin
     * @return the plugin matching the purpose
     * @throws Exception if no plugin was found with the purpose
     */
    public Plugin getPluginForPurpose(final String type) throws Exception { //TODO replace Exception with something less generic
        Iterator iter = loadedPlugins.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, Plugin> entry = (Entry<String, Plugin>) iter.next();
            if (entry.getValue().getPurpose().equals(type)) {
                return entry.getValue();
            }
        }
        throw new Exception("No Plugin found for given purpose");
    }
}
