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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.codesearch.searcher.shared.ResultItem;

/**
 * Provides methods to search the lucene index
 * @author David Froehlich
 */
public class DocumentSearcher {
//TODO add log messages for class
    /** the key with which the location of the index is stored in the configuration file */
    public static final String INDEX_LOCATION_KEY = "indexLocation";
    /** The parser used for parsing search terms to lucene queries */
    private QueryParser parser;
    /** The searcher used for searching the lucene index */
    private IndexSearcher searcher;
    /** The propertyManager used for retrieving information from the configuration */
    private PropertyManager propertyM;

    private static final Log logger = LogFactory.getLog(DocumentSearcher.class);
    /**
     * Creates a new DocumentSearcher instance
     * @throws ConfigurationException if no value for the key specified in the constant INDEX_LOCATION_KEY could be found in the in the configuration via the PropertyManager
     * @throws IOException if the index could not be opened
     */
    public DocumentSearcher() throws ConfigurationException, IOException {
        //Initialize PropertyManager to retrieve indexlocation from the configuraiton
        propertyM = new PropertyManager(); //TODO replace with spring injection
        String indexLocation = propertyM.getSingleLinePropertyValue(INDEX_LOCATION_KEY);
        logger.debug("Index location set to: "+indexLocation);
        //TODO replace with appropriate Analyzer
        parser = new QueryParser(Version.LUCENE_30, "", new StandardAnalyzer(Version.LUCENE_30));
        searcher = new IndexSearcher(FSDirectory.open(new File(indexLocation)), true);
        logger.debug("DocumentSearcher created");
    }

    /**
     * Performs a search against the lucene index and returns each result as an ResultItem
     * @param searchString the String that will be parsed to a query
     * @return the results as ResultItems
     * @throws ParseException if the searchString could not be parsed to a query
     * @throws IOException if the Index could not be read
     */
    public List<ResultItem> getResultsForSearch(String searchString) throws ParseException, IOException {
        LinkedList<ResultItem> results = new LinkedList<ResultItem>();
        Query query = parser.parse(searchString);
        logger.info("Search index with query: "+query.toString());
        //Retrieve all search results from search
        TopDocs topDocs = searcher.search(query, 10000);
        logger.info("Found "+topDocs.scoreDocs.length+" results");
        Document doc;
        //Add each search result in form of a ResultItem to the results-list
        for (ScoreDoc sd : topDocs.scoreDocs) {
            doc = searcher.doc(sd.doc);
            String repo = doc.get(IndexConstants.INDEX_FIELD_REPOSITORY);
            String filePath = doc.get(IndexConstants.INDEX_FIELD_FILEPATH);
            float relevance = sd.score;
            results.add(new ResultItem(filePath, repo, relevance));
        }
        return results;
    }
}
