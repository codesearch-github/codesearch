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

import java.util.List;

import org.codesearch.searcher.shared.FileDto;
import org.codesearch.searcher.shared.SearcherServiceException;
import org.codesearch.searcher.shared.SearchResultDto;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The service used for communication between the GWT javascript client and the server.
 * @author Samuel Kogler
 */
@RemoteServiceRelativePath("searcherservice")
public interface SearcherService extends RemoteService {
    List<SearchResultDto> doSearch(String query, boolean caseSensitive, List<String> selectedRepositories, List<String> selectedRepositoryGroups) throws SearcherServiceException;
    List<String> getAvailableRepositories() throws SearcherServiceException;
    List<String> getAvailableRepositoryGroups() throws SearcherServiceException;
    FileDto getFile(String repository, String filePath) throws SearcherServiceException;
    FileDto getFileForUsageInFile(int usageId, String repository, String filePath) throws SearcherServiceException;
}
