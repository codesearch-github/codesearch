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
 * 
 * @author David Froehlich
 */
public class DocumentSearcher {

    public static final String INDEX_LOCATION_KEY = "indexLocation";
    private QueryParser parser;
    private IndexSearcher searcher;
    private PropertyManager propertyM;
    private String indexLocation;

    /**
     * Creates a new DocumentSearcher instance
     * @throws ConfigurationException if no value for the key specified in the constant INDEX_LOCATION_KEY could be found in the in the configuration via the PropertyManager
     * @throws IOException if the index could not be opened
     */
    public DocumentSearcher() throws ConfigurationException, IOException {
        propertyM = new PropertyManager(); //TODO replace with spring injection
        indexLocation = propertyM.getSingleLinePropertyValue(INDEX_LOCATION_KEY);
        parser = new QueryParser(Version.LUCENE_30, "", new StandardAnalyzer(Version.LUCENE_30));
        searcher = new IndexSearcher(FSDirectory.open(new File(indexLocation)), true);
    }

    /**
     * parses the query to a lucene Query object
     * @param query the string for the query
     * @return the query
     * @throws ParseException if the query could not be parsed
     */
    private Query parseQueryString(String query) throws ParseException {
        Query parsedQuery = parser.parse(query);
        return parsedQuery;
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
        Query query = parseQueryString(searchString);
        TopDocs topDocs = searcher.search(query, 10000);
        Document doc;
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
