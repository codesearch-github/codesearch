
package org.codesearch.commons.configuration.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Provides methods to store and access the indexing status of repositories.
 * Usually, the status is the current revision or -1 if not indexed.
 * @author Stephan Stiboller
 */
public interface IndexStatusManager {

    /**
     * Returns the status of the repository with the specified name.
     * @param repositoryName The name of the repository.
     * @return The status.
     */
    String getStatus(final String repositoryName);

    /**
     * Saves the status of the repository with the specified name.
     * @param repositoryName The name of the repository.
     * @param status The status string(usually a revision).
     * @return The status.
     */
    void setStatus(final String repositoryName, final String status) throws FileNotFoundException, IOException;

    
    List<String> getAllSavedRepositoryNames();
    
    /** 
     * Reloads the properties file to reflect changes.
     */
    void refresh();
}
