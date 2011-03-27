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
@RemoteServiceRelativePath("../gwt.rpc")
public interface SearcherService extends RemoteService {
    /**
     * Executes a lucene search on the server and returns the results as a {@link List} of {@link SearchResultDto}.
     * @param query The given search query.
     * @param caseSensitive Whether the search should be case sensitive.
     * @param selectedRepositories The list of repositories to be searched in.
     * @param selectedRepositoryGroups The list of repository groups to be searched in.
     * @return The results as a {@link List} of {@link SearchResultDto}.
     * @throws SearcherServiceException If an exception occurs on the server.
     */
    List<SearchResultDto> doSearch(String query, boolean caseSensitive, List<String> selectedRepositories, List<String> selectedRepositoryGroups) throws SearcherServiceException;

    /**
     * Returns the repositories that are configured on the server.
     * @return The list of configured repositories.
     * @throws SearcherServiceException If an exception occurs on the server.
     */
    List<String> getAvailableRepositories() throws SearcherServiceException;

    /**
     * Returns the repository groups that are configured on the server.
     * @return The list of configured repository groups.
     * @throws SearcherServiceException If an exception occurs on the server.
     */
    List<String> getAvailableRepositoryGroups() throws SearcherServiceException;

    /**
     * Retrieves a file identified by the given repository name and filepath.
     * @param repository The given repository name.
     * @param filePath The given file path.
     * @return The {@link FileDto} containing the file content and all necessary information about the file.
     * @throws SearcherServiceException If an exception occurs on the server.
     */
    FileDto getFile(String repository, String filePath) throws SearcherServiceException;

    /**
     * Resolves the usage with the given id in the given file and returns a {@link SearchResultDto}
     * that points to the file the usage is referring to.
     * @param usageId The given usage id.
     * @param repository The repository of the given file.
     * @param filePath The path of the given file.
     * @return The {@link SearchResultDto} the usage refors to.
     * @throws SearcherServiceException If an exception occurs on the server.
     */
    SearchResultDto resolveUsage(int usageId, String repository, String filePath) throws SearcherServiceException;
}
