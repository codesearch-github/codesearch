package org.codesearch.searcher.client.rpc;

import java.util.List;
import java.util.Set;

import org.codesearch.searcher.shared.FileDto;
import org.codesearch.searcher.shared.SearchResultDto;
import org.codesearch.searcher.shared.SearchType;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SearcherServiceAsync {

	void doSearch(String query, boolean caseSensitive, SearchType searchType,
			Set<String> selection, AsyncCallback<List<SearchResultDto>> callback);

	void getAvailableRepositories(AsyncCallback<List<String>> callback);

	void getAvailableRepositoryGroups(AsyncCallback<List<String>> callback);

	void getFile(String repository, String filePath,
			AsyncCallback<FileDto> callback);

	void resolveUsage(int usageId, String repository, String filePath,
			AsyncCallback<SearchResultDto> callback);

}
