/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.indexer.exceptions;

/**
 *
 * @author Samuel Kogler
 */
public class JobExecutionException extends Exception {

    /**
     * Creates a new instance of <code>JobExecutionException</code> without detail message.
     */
    public JobExecutionException() {
    }


    /**
     * Constructs an instance of <code>JobExecutionException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public JobExecutionException(String msg) {
        super(msg);
    }
}
