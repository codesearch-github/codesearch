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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.searcher.server;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.apache.lucene.queryParser.ParseException;
import org.codesearch.searcher.shared.SearchResultDto;

/**
 * Util class that can be used to perform lucene searches against the index.
 * @author David Froehlich
 * @author Samuel Kogler
 */
public interface DocumentSearcher {
    /**
     * parses the search term to a lucene conform query keeping in mind the several options
     * @param query the search query
     * @return the lucene conform query
     */
    String parseQuery(String query, boolean caseSensitive, Set<String> repositoryNames, Set<String> repositoryGroupNames);

    /**
     * Performs a search against the lucene index and returns each result as an ResultItem
     * @param searchString the String that will be parsed to a query
     * @return the results as ResultItems
     * @throws ParseException if the searchString could not be parsed to a query
     * @throws IOException if the Index could not be read
     */
    List<SearchResultDto> search(String searchString, boolean caseSensitive, Set<String> repositoryNames, Set<String> repositoryGroupNames, int maxResults) throws ParseException, IOException, InvalidIndexException;

    List<String> suggestSearchNames(String searchString, boolean caseSensitive, Set<String> repositoryNames, Set<String> repositoryGroupNames) throws ParseException, IOException, InvalidIndexException;

    void refreshIndex() throws InvalidIndexException;
}
