package org.codesearch.searcher.server;

/**
 * Exception thrown when the index is not valid or could not be found.
 * @author Samuel Kogler
 */
public class InvalidIndexException extends Exception {

    /**
     * Creates a new instance of <code>InvalidIndexException</code> without detail message.
     */
    public InvalidIndexException() {
    }

    /**
     * Constructs an instance of <code>InvalidIndexException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public InvalidIndexException(String msg) {
        super(msg);
    }

}
