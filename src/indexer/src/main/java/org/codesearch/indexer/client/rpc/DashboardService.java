/**
 * 
 */
package org.codesearch.indexer.client.rpc;

import org.codesearch.indexer.shared.DashboardData;
import org.codesearch.indexer.shared.DashboardServiceException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Samuel Kogler
 * The service used the retrieve the data for the dashboard.
 */
@RemoteServiceRelativePath("../dashboard.rpc")
public interface DashboardService extends RemoteService {
	
	/**
	 * Retrieves the data for the dashboard.
	 * @return The data for the dashboard.
	 */
	DashboardData getData() throws DashboardServiceException;
	
}