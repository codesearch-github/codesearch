/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.searcher.server;

import java.io.IOException;
import java.util.List;
import org.apache.lucene.queryParser.ParseException;
import org.codesearch.searcher.shared.SearchResultDto;

/**
 *
 * @author David Froehlich
 */
public interface DocumentSearcher {
    /**
     * parses the search term to a lucene conform query keeping in mind the several options
     * @param term the search term
     * @return the lucene conform query
     */
    String parseQuery(String term, boolean caseSensitive, List<String> repositoryNames, List<String> repositoryGroupNames);

    /**
     * Performs a search against the lucene index and returns each result as an ResultItem
     * @param searchString the String that will be parsed to a query
     * @return the results as ResultItems
     * @throws ParseException if the searchString could not be parsed to a query
     * @throws IOException if the Index could not be read
     */
    List<SearchResultDto> search(String searchString, boolean caseSensitive, List<String> repositoryNames, List<String> repositoryGroupNames) throws ParseException, IOException, InvalidIndexException;

    List<String> suggestSearchNames(String searchString, boolean caseSensitive, List<String> repositoryNames, List<String> repositoryGroupNames) throws ParseException, IOException, InvalidIndexException;

    void refreshIndex() throws InvalidIndexException;
}
