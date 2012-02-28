package org.codesearch.commons.configuration.dto;

/**
 * Identifies different indexing operations.
 * @author Samuel Kogler
 */
@Deprecated
public enum IndexingTaskType {
    CLEAR, //Clears the entire index
    INDEX // Index the repository
}
