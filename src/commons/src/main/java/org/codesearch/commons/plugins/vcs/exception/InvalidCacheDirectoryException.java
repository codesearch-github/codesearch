package org.codesearch.commons.plugins.vcs.exception;

import org.codesearch.commons.plugins.vcs.VersionControlPlugin;

/**
 * Thrown when the cache directory passed to a {@link VersionControlPlugin}
 * is invalid.
 */
public class InvalidCacheDirectoryException extends Exception {

    private static final long serialVersionUID = -6360334452982493433L;

    public InvalidCacheDirectoryException() {
    }

    /**
     * @param message
     */
    public InvalidCacheDirectoryException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public InvalidCacheDirectoryException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public InvalidCacheDirectoryException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

}
