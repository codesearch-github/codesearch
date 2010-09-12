/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.searcher.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.codesearch.searcher.shared.SearchResultDto;

/**
 *
 * @author Samuel Kogler
 */
public interface SearcherServiceAsync {
    public void doSearch(String query, AsyncCallback<SearchResultDto[]> asyncCallback);
}
