/**
 * 
 */
package org.codesearch.indexer.shared;

/**
 * @author Samuel Kogler
 * Is thrown in the RPC service when an exception occurs on the server. 
 * Mostly used to wrap other exceptions.
 */
public class ManualIndexingServiceException extends Exception {

	/** . */
    private static final long serialVersionUID = -2617014263508638043L;

    /**
	 * 
	 */
	public ManualIndexingServiceException() {
		super();
	}

	/**
	 * @param msg
	 */
	public ManualIndexingServiceException(String msg) {
		super(msg);
	}

}
