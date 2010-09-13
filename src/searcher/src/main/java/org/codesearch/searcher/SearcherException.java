package org.codesearch.searcher;

/**
 *
 * @author Samuel Kogler
 */
public class SearcherException extends Exception {

    /**
     * Creates a new instance of <code>SearcherException</code> without detail message.
     */
    public SearcherException() {
    }


    /**
     * Constructs an instance of <code>SearcherException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public SearcherException(String msg) {
        super(msg);
    }
}
