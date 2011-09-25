/**
 * Copyright 2010 David Froehlich <david.froehlich@businesssoftware.at>, Samuel Kogler <samuel.kogler@gmail.com>, Stephan Stiboller
 * <stistc06@htlkaindorf.at>
 * 
 * This file is part of Codesearch.
 * 
 * Codesearch is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * Codesearch is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Codesearch. If not, see <http://www.gnu.org/licenses/>.
 */
package org.codesearch.searcher.server;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.constants.IndexConstants;
import org.codesearch.searcher.shared.SearchResultDto;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DocumentSearcherImpl implements DocumentSearcher {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(DocumentSearcherImpl.class);
    /** The parser used for parsing search terms to lucene queries */
    private QueryParser queryParser;
    /** The parser used for parsing search terms to lucene queries - case sensitive */
    private QueryParser queryParserCaseSensitive;
    /** The searcher used for searching the lucene index */
    private IndexSearcher indexSearcher;
    /** Whether the searcher has been initialized. **/
    private boolean searcherInitialized = false;
    /** The location of the index. **/
    private File indexLocation;
    /** The used configuration reader. */
    private ConfigurationReader configurationReader;

    /**
     * Creates a new DocumentSearcher instance
     * 
     * @throws ConfigurationException if no value for the key specified in the constant INDEX_LOCATION_KEY could be found in the in the
     *             configuration via the XmlConfigurationReader
     * @throws IOException if the index could not be opened
     */
    @Inject
    public DocumentSearcherImpl(ConfigurationReader configurationReader) {
        // Retrieve index location from the configuration
        indexLocation = configurationReader.getIndexLocation();
        LOG.debug("Index location set to: " + indexLocation);
        // FIXME critical: use analyzers and fields provided by plugins!
        // TODO make or find proper analyzers for search
        queryParser = new QueryParser(IndexConstants.LUCENE_VERSION, IndexConstants.INDEX_FIELD_CONTENT + "_lc",
                new LowerCaseWhiteSpaceAnalyzer());
        queryParser.setAllowLeadingWildcard(true);
        queryParser.setLowercaseExpandedTerms(false);
        queryParserCaseSensitive = new QueryParser(IndexConstants.LUCENE_VERSION, IndexConstants.INDEX_FIELD_CONTENT,
                new WhitespaceAnalyzer(IndexConstants.LUCENE_VERSION));
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

    /** {@inheritDoc} */
    @Override
    public synchronized List<SearchResultDto> search(String searchString, boolean caseSensitive, Set<String> repositoryNames,
            Set<String> repositoryGroupNames, int maxResults) throws ParseException, IOException, InvalidIndexException {
        return performLuceneSearch(searchString, caseSensitive, repositoryNames, repositoryGroupNames, maxResults);
    }

//    /** {@inheritDoc} */
//    @Override
//    public synchronized List<String> suggestSearchNames(String searchString, boolean caseSensitive, Set<String> repositoryNames,
//            Set<String> repositoryGroupNames) throws ParseException, IOException, InvalidIndexException {
//        List<SearchResultDto> results = performLuceneSearch(searchString, caseSensitive, repositoryNames, repositoryGroupNames, 10);
//
//        return null;
//    }

    /** {@inheritDoc} */
    @Override
    public synchronized String parseQuery(String query, boolean caseSensitive, Set<String> repositoryNames, Set<String> repositoryGroupNames) {
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
        repoQuery.append(":("); // this is because the lucene query is sad

        for (String repo : repositoryNames) {
            repoQuery.append(repo);
            repoQuery.append(" OR ");
        }
        repoQuery = new StringBuilder(repoQuery.substring(0, repoQuery.length() - 4));
        repoQuery.append(")");

        return query + repoQuery.toString();
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void refreshIndex() throws InvalidIndexException {
        try {
            // Calling reopen instead of new initialization for performance reasons
            indexSearcher = new IndexSearcher(indexSearcher.getIndexReader().reopen(true));
        } catch (CorruptIndexException ex) {
            throw new InvalidIndexException("Could not refresh the index because it is corrupt: " + ex);
        } catch (IOException ex) {
            throw new InvalidIndexException("Could not refresh the index: " + ex);
        }
    }

    private synchronized List<SearchResultDto> performLuceneSearch(String searchString, boolean caseSensitive, Set<String> repositoryNames,
            Set<String> repositoryGroupNames, int maxResults) throws ParseException, IOException, InvalidIndexException {
        if (!searcherInitialized) {
            initSearcher();
        }
        List<SearchResultDto> results = new LinkedList<SearchResultDto>();
        String finalSearchString = parseQuery(searchString, caseSensitive, repositoryNames, repositoryGroupNames);
        Query query = null;
        if (caseSensitive) {
            query = queryParserCaseSensitive.parse(finalSearchString);
        } else {
            finalSearchString = finalSearchString.replace(IndexConstants.INDEX_FIELD_FILEPATH + ":",
                    IndexConstants.INDEX_FIELD_FILEPATH + "_lc:").replace(IndexConstants.INDEX_FIELD_CONTENT + ":",
                    IndexConstants.INDEX_FIELD_CONTENT + "_lc:");
            query = queryParser.parse(finalSearchString.toLowerCase());
        }

        LOG.info("Searching index with query: " + query.toString());
        // Retrieve all search results from search
        TopDocs topDocs = indexSearcher.search(query, maxResults);
        LOG.info("Found " + topDocs.scoreDocs.length + " results");
        Document doc;
        // Add each search result in form of a ResultItem to the results-list
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

    private synchronized void initSearcher() throws InvalidIndexException {
        try {
            if (searcherInitialized) {
                indexSearcher.close();
            }
            indexSearcher = new IndexSearcher(FSDirectory.open(indexLocation), true);
            searcherInitialized = true;
        } catch (IOException exc) {
            searcherInitialized = false;
            throw new InvalidIndexException("No valid index found at: " + indexLocation + "\n" + exc);
        }
    }
}
