package org.codesearch.searcher.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import java.util.List;
import org.codesearch.searcher.shared.ResultItem;

/**
 *
 * @author Samuel Kogler
 */
@RemoteServiceRelativePath("searcherservice")
public interface SearcherService extends RemoteService {
    public List<ResultItem> doSearch(String query);
}
