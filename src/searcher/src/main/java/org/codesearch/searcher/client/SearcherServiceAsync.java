/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.searcher.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 *
 * @author Samuel Kogler
 */
public interface SearcherServiceAsync {
    public void doSearch(String query, AsyncCallback<String> callback);
}
