/**
 * 
 */
package org.codesearch.indexer.client.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Samuel Kogler
 *
 */
public interface LogServiceAsync {

    /**
     * 
     * @see org.codesearch.indexer.client.rpc.LogService#getLog()
     */
    void getLog(AsyncCallback<List<String>> callback);

}
