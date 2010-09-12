package org.codesearch.searcher.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import java.util.List;
import org.codesearch.searcher.shared.SearchResultDto;

/**
 *
 * @author Samuel Kogler
 */
@RemoteServiceRelativePath("searcherservice")
public interface SearcherService extends RemoteService {
    public SearchResultDto[] doSearch(String query);
}
