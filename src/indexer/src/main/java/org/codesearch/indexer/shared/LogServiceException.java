/**
 * 
 */
package org.codesearch.indexer.shared;

/**
 * @author Samuel Kogler
 * Generic exception for the log service.
 */
public class LogServiceException extends Exception {

    /**
     * 
     */
    public LogServiceException() {
        super();
    }

    /**
     * @param message
     */
    public LogServiceException(String message) {
        super(message);
    }
}
