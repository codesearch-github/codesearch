
package org.codesearch.commons.configuration.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Provides several methods to access properties.
 * @author Stephan Stiboller
 */
public interface PropertiesManager {

    /**
     * Gets a new value for the specified key
     * @param key
     * @return The value of the key.
     */
    String getValue(final String key);

    /**
     * Sets a new value for the specified key
     * @param key
     * @param value
     */
    void setValue(final String key, final String value) throws FileNotFoundException, IOException;

    
    List<String> getAllKeys();
    
    /** 
     * Reloads the properties file to reflect changes.
     */
    void refresh();
}
