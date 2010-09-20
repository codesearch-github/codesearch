/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins;

/**
 *
 * @author David Froehlich
 */
public class PluginLoaderException extends Exception {

    /**
     * Creates a new instance of PluginLoaderException that calls the constructor of Exception with
     * the given value
     * @param string for the superclass constructor
     */
    public PluginLoaderException(String string) {
        super(string);
    }

    /**
     * Creates a new instance of PluginLoaderException
     */
    public PluginLoaderException() {
    }
}
