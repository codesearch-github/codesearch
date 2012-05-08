package org.codesearch.searcher.server;

import org.codesearch.commons.configuration.properties.IndexStatusManager;

/**
 * Provides some common methods for the searcher.
 * @author Samuel Kogler
 */
public interface SearcherUtil {

    /**
     * Re-initializes {@link DocumentSearcher} and {@link IndexStatusManager} 
     * to recognize changes to the index.
     * @throws InvalidIndexException If the index could not be refreshed because it is invalid.
     */
    void refreshIndexInformation() throws InvalidIndexException;
}
