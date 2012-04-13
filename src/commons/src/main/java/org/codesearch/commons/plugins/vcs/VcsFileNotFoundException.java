package org.codesearch.commons.plugins.vcs;

/**
 * Thrown by {@link VersionControlPlugin} implementations if the specified
 * file could not be found.
 * @author Samuel Kogler
 */
public class VcsFileNotFoundException extends Exception {

    /**
     * Constructs an instance of <code>VcsFileNotFoundException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public VcsFileNotFoundException(String msg) {
        super(msg);
    }
}
