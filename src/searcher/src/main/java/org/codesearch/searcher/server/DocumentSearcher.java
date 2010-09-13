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
package org.codesearch.searcher.server;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.codesearch.commons.configreader.xml.PropertyManager;
import org.codesearch.commons.constants.IndexConstants;
import org.codesearch.searcher.shared.SearchResultDto;

/**
 * Provides methods to search the lucene index
 * @author David Froehlich
 */
public class DocumentSearcher {

    /** the key with which the location of the index is stored in the configuration file */
    public static final String INDEX_LOCATION_KEY = "indexLocation";
    /** The logger. */
    private static final Logger LOG = Logger.getLogger(DocumentSearcher.class);
    /** The parser used for parsing search terms to lucene queries */
    private QueryParser queryParser;
    /** The searcher used for searching the lucene index */
    private IndexSearcher indexSearcher;
    /** Whether the searcher has been initialized. **/
    private boolean searcherInitialized = false;
    /** The location of the index. **/
    private String indexLocation;

    /**
     * Creates a new DocumentSearcher instance
     * @throws ConfigurationException if no value for the key specified in the constant INDEX_LOCATION_KEY could be found in the in the configuration via the PropertyManager
     * @throws IOException if the index could not be opened
     */
    public DocumentSearcher(PropertyManager propertyManager) throws ConfigurationException {
        // Retrieve index location from the configuration
        indexLocation = propertyManager.getSingleLinePropertyValue(INDEX_LOCATION_KEY);
        LOG.debug("Index location set to: " + indexLocation);
        //TODO replace with appropriate Analyzer
        queryParser = new QueryParser(Version.LUCENE_30, "", new StandardAnalyzer(Version.LUCENE_30));
        try {
            initSearcher();
        } catch(IOException ex)  {}
        LOG.debug("DocumentSearcher created");
    }

    /**
     * Performs a search against the lucene index and returns each result as an ResultItem
     * @param searchString the String that will be parsed to a query
     * @return the results as ResultItems
     * @throws ParseException if the searchString could not be parsed to a query
     * @throws IOException if the Index could not be read
     */
    public List<SearchResultDto> search(String searchString) throws ParseException, IOException {
        if (!searcherInitialized) {
            initSearcher();
        }
        LinkedList<SearchResultDto> results = new LinkedList<SearchResultDto>();
        Query query = queryParser.parse(searchString);
        LOG.info("Search index with query: " + query.toString());
        //Retrieve all search results from search
        TopDocs topDocs = indexSearcher.search(query, 10000);
        LOG.info("Found " + topDocs.scoreDocs.length + " results");
        Document doc;
        //Add each search result in form of a ResultItem to the results-list
        for (ScoreDoc sd : topDocs.scoreDocs) {
            doc = indexSearcher.doc(sd.doc);
            SearchResultDto searchResult = new SearchResultDto();
            searchResult.setRepository(doc.get(IndexConstants.INDEX_FIELD_REPOSITORY));
            searchResult.setFilePath(doc.get(IndexConstants.INDEX_FIELD_FILEPATH));
            searchResult.setRelevance(sd.score);
            results.add(searchResult);
        }
        return results;
    }

    private void initSearcher() throws IOException {
        try {
            indexSearcher = new IndexSearcher(FSDirectory.open(new File(indexLocation)), true);
            searcherInitialized = true;
        } catch (IOException exc) {
            searcherInitialized = false;
            LOG.warn("No valid index found at: " + indexLocation);
            LOG.warn("Will try to re-initialize at next search operation");
            throw exc;
        }
    }
}
