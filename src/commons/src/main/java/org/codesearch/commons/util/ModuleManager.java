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
package org.codesearch.commons.util;

import java.util.LinkedList;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.codesearch.commons.configreader.xml.PropertyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * Used to parse the configuration for a module.
 * @author Samuel Kogler
 */
public class ModuleManager {

    /** The filename of the default module configuration. */
    private static final String DEFAULT_CONFIG_FILENAME = "module-config.xml";
    /** The property manager used to read the configuration. */
    @Autowired
    private PropertyManager propertyManager;
    /** The initialized application context. */
    private ApplicationContext applicationContext;

    /**
     * Returns the locations of the spring configuration files
     * defined in the module configuration.
     * @return The locations of the spring configuration files.
     */
    public List<String> getSpringConfigLocations() throws ConfigurationException {
        List<String> springConfigLocations = new LinkedList<String>();
        propertyManager.setConfigpath(DEFAULT_CONFIG_FILENAME);
        springConfigLocations.addAll(propertyManager.getSingleLinePropertyValueList("springConfigLocation"));
        return springConfigLocations;
    }

}
