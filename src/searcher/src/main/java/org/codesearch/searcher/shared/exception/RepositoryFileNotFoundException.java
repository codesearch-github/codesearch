package org.codesearch.searcher.shared.exception;

import java.io.Serializable;

/**
 * Indicates that the specified file was not found in the specified repository on the server.
 * @author Samuel Kogler
 */
public class RepositoryFileNotFoundException extends Exception implements Serializable {

    private static final long serialVersionUID = 649789785944976640L;
    
}
