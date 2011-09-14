/**
 * 
 */
package org.codesearch.indexer.shared;

/**
 * @author Samuel Kogler
 * Is thrown in the RPC service when an exception occurs on the server. 
 * Mostly used to wrap other exceptions.
 */
public class DashboardServiceException extends Exception {

	/**
	 * 
	 */
	public DashboardServiceException() {
		super();
	}

	/**
	 * @param msg
	 */
	public DashboardServiceException(String msg) {
		super(msg);
	}

}
