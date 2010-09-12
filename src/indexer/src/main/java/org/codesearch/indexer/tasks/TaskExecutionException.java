/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.indexer.tasks;

/**
 *
 * @author David Froehlich
 */
public class TaskExecutionException extends Exception {

    public TaskExecutionException(String string) {
        super(string);
    }

    public TaskExecutionException() {
        super();
    }
}
