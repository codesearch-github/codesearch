package org.codesearch.commons.configuration.xml.dto;

/**
 * Identifies different indexing operations.
 * @author Samuel Kogler
 */
@Deprecated
public enum IndexingTaskType {
    CLEAR, //Clears the entire index
    INDEX // Index the repository
}