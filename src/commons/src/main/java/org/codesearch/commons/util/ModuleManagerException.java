package org.codesearch.commons.util;

/**
 * Exception thrown in the Module Manager.
 * @author Samuel Kogler
 */
public class ModuleManagerException extends Exception {

    /**
     * Creates a new instance of <code>ModuleManagerException</code> without detail message.
     */
    public ModuleManagerException() {
    }

    /**
     * Constructs an instance of <code>ModuleManagerException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ModuleManagerException(String msg) {
        super(msg);
    }
}
