package org.codesearch.commons.plugins.vcs;

/**
 * An Exception that can be thrown by the version control plugins.
 * @author Samuel Kogler
 */
public class VersionControlPluginException extends Exception {

    /**
     * Creates a new instance of <code>VersionControlPluginException</code> without detail message.
     */
    public VersionControlPluginException() {
    }


    /**
     * Constructs an instance of <code>VersionControlPluginException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public VersionControlPluginException(String msg) {
        super(msg);
    }
}
