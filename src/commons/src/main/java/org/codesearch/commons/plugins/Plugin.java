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

/**
 * The base interface for all plugins.
 * @author David Froehlich
 */
public interface Plugin {
    /**
     * Returns the purpose of this plugin,
     * Can be used to distinguish between multiple plugins that have the same base-functionality but are used in different situations
     * for instance getPurpose of a VersionControlPlugin could either return 'SVN' or 'Bazaar' and so forth
     * @return the purpose of the plugin
     */
    String getPurposes();

    /**
     * Returns the version of the plugin
     * @return the version of the plugin
     */
    String getVersion();
}
