/**
 * Copyright 2010 David Froehlich   <david.froehlich@businesssoftware.at>,
 *                Samuel Kogler     <samuel.kogler@gmail.com>,
 *                Stephan Stiboller <stistc06@htlkaindorf.at>
 *
 * This file is part of Codesearch.
 *
 * Codesearch is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Codesearch is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Codesearch.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.codesearch.searcher.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.List;
import java.util.Set;
import org.codesearch.searcher.shared.FileDto;
import org.codesearch.searcher.shared.SearchResultDto;
import org.codesearch.searcher.shared.SearchType;

public interface SearcherServiceAsync {
	void doSearch(String query, boolean caseSensitive, SearchType searchType,
			Set<String> selection, int maxResults, AsyncCallback<List<SearchResultDto>> callback);

	void getAvailableRepositories(AsyncCallback<List<String>> callback);

	void getAvailableRepositoryGroups(AsyncCallback<List<String>> callback);

	void getFile(String repository, String filePath, boolean highlight, boolean insertCodeNavigationLinks,
			AsyncCallback<FileDto> callback);

	void resolveUsage(int usageId, String repository, String filePath,
			AsyncCallback<SearchResultDto> callback);
}
