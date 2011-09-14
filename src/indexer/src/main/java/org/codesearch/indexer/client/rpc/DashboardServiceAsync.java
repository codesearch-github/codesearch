/**
 * 
 */
package org.codesearch.indexer.client.rpc;

import org.codesearch.indexer.shared.DashboardData;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Samuel Kogler
 *
 */
public interface DashboardServiceAsync {
	void getData(AsyncCallback<DashboardData> callback);
}
