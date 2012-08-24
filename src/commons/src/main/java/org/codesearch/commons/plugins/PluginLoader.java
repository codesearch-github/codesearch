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

/**
 * Responsible for loading, instancing and finding all plugins.
 * @author Samuel Kogler
 */
public interface PluginLoader {

    /**
     * Returns a plugin of the given type whose purposes contain the given purpose.
     * 
     * @param purpose The purpose
     * @return The plugin matching the purpose, null if not found.
     */
    <T extends Plugin> T getPlugin(final Class<T> clazz, final String purpose);
    
    /**
     * Returns all implementations for the given plugin base class.
     *
     * @param clazz The base interface
     * @return A list of plugin implementations, empty list if none are found
     */
    <T extends Plugin> List<T> getAllPluginsOfClass(final Class<T> clazz);
}
