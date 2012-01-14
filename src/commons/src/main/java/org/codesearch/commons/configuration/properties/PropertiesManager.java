
package org.codesearch.commons.configuration.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import javax.inject.Singleton;

/**
 * Provides several methods to access properties.
 * @author Stephan Stiboller
 */
@Singleton
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

}
