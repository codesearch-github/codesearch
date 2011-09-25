/**
 * 
 */
package org.codesearch.indexer.shared;

/**
 * @author Samuel Kogler
 * Generic exception for the log service.
 */
public class LogServiceException extends Exception {

    /** . */
    private static final long serialVersionUID = 5478204580908243801L;

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
