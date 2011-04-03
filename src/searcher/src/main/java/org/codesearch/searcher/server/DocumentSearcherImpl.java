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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.configuration.xml.XmlConfigurationReaderConstants;
import org.codesearch.commons.constants.IndexConstants;
import org.codesearch.searcher.server.util.STAlternativeSuggestor;
import org.codesearch.searcher.shared.SearchResultDto;

/**
 * Provides methods to search the index.
 * @author David Froehlich
 */
@Singleton
public class DocumentSearcherImpl implements DocumentSearcher {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(DocumentSearcherImpl.class);
    /** The parser used for parsing search terms to lucene queries */
    private QueryParser queryParser;
    /** The parser used for parsing search terms to lucene queries - case sensitive*/
    private QueryParser queryParserCaseSensitive;
    /** The searcher used for searching the lucene index */
    private IndexSearcher indexSearcher;
    /** Whether the searcher has been initialized. **/
    private boolean searcherInitialized = false;
    /** The location of the index. **/
    private String indexLocation;
    /** Used completer for search term autocompletion functionality */
    private STAlternativeSuggestor autocompleter;
    private ConfigurationReader configurationReader;

    /**
     * Creates a new DocumentSearcher instance
     * @throws ConfigurationException if no value for the key specified in the constant INDEX_LOCATION_KEY could be found in the in the configuration via the XmlConfigurationReader
     * @throws IOException if the index could not be opened
     */
    @Inject
    public DocumentSearcherImpl(ConfigurationReader configurationReader) {
        // Retrieve index location from the configuration
        indexLocation = configurationReader.getValue(XmlConfigurationReaderConstants.INDEX_LOCATION);
        LOG.debug("Index location set to: " + indexLocation);
        //TODO make or find proper analyzers for search
        queryParser = new QueryParser(Version.LUCENE_30, IndexConstants.INDEX_FIELD_CONTENT + "_lc", new LowerCaseWhiteSpaceAnalyzer());
        queryParser.setAllowLeadingWildcard(true);
        queryParser.setLowercaseExpandedTerms(false);
        queryParserCaseSensitive = new QueryParser(Version.LUCENE_30, IndexConstants.INDEX_FIELD_CONTENT, new WhitespaceAnalyzer());
        queryParserCaseSensitive.setAllowLeadingWildcard(true);
        queryParserCaseSensitive.setLowercaseExpandedTerms(false);
        try {
            initSearcher();
        } catch (InvalidIndexException ex) {
            LOG.warn(ex);
            LOG.warn("Will try to re-initialize searcher at the first search operation");
        }
        LOG.debug("DocumentSearcher created");
    }

    /**
     * Performs a search against the lucene index and returns each result as an ResultItem
     * @param searchString the String that will be parsed to a query
     * @return the results as ResultItems
     * @throws ParseException if the searchString could not be parsed to a query
     * @throws IOException if the Index could not be read
     */
    @Override
    public synchronized List<SearchResultDto> search(String searchString, boolean caseSensitive, List<String> repositoryNames, List<String> repositoryGroupNames) throws ParseException, IOException, InvalidIndexException {
        return performLuceneSearch(searchString, caseSensitive, repositoryNames, repositoryGroupNames, 1000);
    }

    @Override
    public synchronized List<String> suggestSearchNames(String searchString, boolean caseSensitive, List<String> repositoryNames, List<String> repositoryGroupNames) throws ParseException, IOException, InvalidIndexException {
        List<SearchResultDto> results = performLuceneSearch(searchString, caseSensitive, repositoryNames, repositoryGroupNames, 10);

        return null;
    }

    private synchronized List<SearchResultDto> performLuceneSearch(String searchString, boolean caseSensitive, List<String> repositoryNames, List<String> repositoryGroupNames, int maxResults) throws ParseException, IOException, InvalidIndexException {
        if (!searcherInitialized) {
            initSearcher();
        }
        List<SearchResultDto> results = new LinkedList<SearchResultDto>();
        String finalSearchString = parseQuery(searchString, caseSensitive, repositoryNames, repositoryGroupNames);
        Query query = null;
        if (caseSensitive) {
            query = queryParserCaseSensitive.parse(finalSearchString);
        } else {
            finalSearchString = finalSearchString.replace(IndexConstants.INDEX_FIELD_FILEPATH + ":", IndexConstants.INDEX_FIELD_FILEPATH + "_lc:").replace(IndexConstants.INDEX_FIELD_CONTENT + ":",
                    IndexConstants.INDEX_FIELD_CONTENT + "_lc:");
            query = queryParser.parse(finalSearchString.toLowerCase());
        }

        LOG.info("Searching index with query: " + query.toString());
        //Retrieve all search results from search
        TopDocs topDocs = indexSearcher.search(query, maxResults);
        LOG.info("Found " + topDocs.scoreDocs.length + " results");
        Document doc;
        //Add each search result in form of a ResultItem to the results-list
        for (ScoreDoc sd : topDocs.scoreDocs) {
            doc = indexSearcher.doc(sd.doc);
            SearchResultDto searchResult = new SearchResultDto();
            searchResult.setRepository(doc.get(IndexConstants.INDEX_FIELD_REPOSITORY));
            searchResult.setFilePath(doc.get(IndexConstants.INDEX_FIELD_FILEPATH));
            searchResult.setRevision(doc.get(IndexConstants.INDEX_FIELD_REVISION));
            searchResult.setRelevance(sd.score);
            results.add(searchResult);
        }
        return results;
    }

    /**
     * parses the search term to a lucene conform query keeping in mind the several options
     * @param term the search term
     * @return the lucene conform query
     */
    @Override
    public synchronized String parseQuery(String term, boolean caseSensitive, List<String> repositoryNames, List<String> repositoryGroupNames) {
        //TODO rename method, same name as lucene and make private after finished testing
        return appendRepositoriesToQuery(term, repositoryNames, repositoryGroupNames);
    }

    private synchronized String appendRepositoriesToQuery(String query, List<String> repositoryNames, List<String> repositoryGroupNames) {
        for (String repoGroup : repositoryGroupNames) {
            for (String repo : configurationReader.getRepositoriesForGroup(repoGroup)) {
                repositoryNames.add(repo);
            }
        }

        if (repositoryNames.isEmpty()) {
            return query;
        }

        StringBuilder repoQuery = new StringBuilder();
        repoQuery.append(" AND ");
        repoQuery.append(IndexConstants.INDEX_FIELD_REPOSITORY);
        repoQuery.append(":("); //this is because the lucene query is sad

        for (String repo : repositoryNames) {
            repoQuery.append(repo);
            repoQuery.append(" OR ");
        }
        repoQuery = new StringBuilder(repoQuery.substring(0, repoQuery.length() - 4));
        repoQuery.append(")");

        return query + repoQuery.toString();
    }

    private synchronized void initSearcher() throws InvalidIndexException {
        try {
            if (searcherInitialized) {
                indexSearcher.close();
            }
            indexSearcher = new IndexSearcher(FSDirectory.open(new File(indexLocation)), true);
            searcherInitialized = true;
        } catch (IOException exc) {
            searcherInitialized = false;
            throw new InvalidIndexException("No valid index found at: " + indexLocation + "\n" + exc);
        }
    }

    @Override
    public synchronized void refreshIndex() throws InvalidIndexException {
        initSearcher();
    }
}
