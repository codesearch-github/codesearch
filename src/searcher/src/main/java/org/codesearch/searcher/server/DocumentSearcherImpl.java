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
import java.util.Iterator;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldPluginLoader;
import org.codesearch.commons.plugins.lucenefields.SimpleSourceCodeAnalyzer;

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
    public DocumentSearcherImpl(ConfigurationReader configurationReader, LuceneFieldPluginLoader luceneFieldPluginLoader) {
        // Retrieve index location from the configuration
        indexLocation = configurationReader.getIndexLocation();
        LOG.debug("Index location set to: " + indexLocation);

        queryParser = new QueryParser(IndexConstants.LUCENE_VERSION, IndexConstants.INDEX_FIELD_CONTENT + IndexConstants.LC_POSTFIX,
                luceneFieldPluginLoader.getPerFieldAnalyzerWrapper(false));
        queryParser.setAllowLeadingWildcard(true);
        queryParser.setDefaultOperator(QueryParser.Operator.AND);
        queryParser.setLowercaseExpandedTerms(false);
        queryParserCaseSensitive = new QueryParser(IndexConstants.LUCENE_VERSION, IndexConstants.INDEX_FIELD_CONTENT, 
                luceneFieldPluginLoader.getPerFieldAnalyzerWrapper(true));
        queryParserCaseSensitive.setAllowLeadingWildcard(true);
        queryParserCaseSensitive.setDefaultOperator(QueryParser.Operator.AND);
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
        if (!searcherInitialized) {
            initSearcher();
        }
        List<SearchResultDto> results = new LinkedList<SearchResultDto>();
        String finalSearchString = parseQuery(searchString, repositoryNames, repositoryGroupNames);
        Query query = null;
        if (caseSensitive) {
            query = queryParserCaseSensitive.parse(finalSearchString);
        } else {
            //TODO: make this compatible with plugins
            finalSearchString = finalSearchString.
                    replace(IndexConstants.INDEX_FIELD_FILEPATH + ":", IndexConstants.INDEX_FIELD_FILEPATH + IndexConstants.LC_POSTFIX + ":").
                    replace(IndexConstants.INDEX_FIELD_CONTENT  + ":", IndexConstants.INDEX_FIELD_CONTENT  + IndexConstants.LC_POSTFIX + ":").
                    replace(IndexConstants.INDEX_FIELD_FILENAME + ":", IndexConstants.INDEX_FIELD_FILENAME + IndexConstants.LC_POSTFIX + ":");
            query = queryParser.parse(finalSearchString.toLowerCase());
        }

        LOG.info("Searching index with query: " + query.toString());
        // Retrieve all search results from search
        TopDocs topDocs = indexSearcher.search(query, maxResults);
        LOG.info("Found " + topDocs.scoreDocs.length + " results");
        Document doc;
        
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

    /**
     * parses the search term to a lucene conform query keeping in mind the several options
     * @param query the search query
     * @return the lucene conform query
     */
    private String parseQuery(String query, Set<String> repositoryNames, Set<String> repositoryGroupNames) {
        for (String repoGroup : repositoryGroupNames) {
            for (String repo : configurationReader.getRepositoriesForGroup(repoGroup)) {
                repositoryNames.add(repo);
            }
        }

        if (repositoryNames.isEmpty()) {
            return query;
        }

        StringBuilder repoQuery = new StringBuilder();
        repoQuery.append(" +");
        repoQuery.append(IndexConstants.INDEX_FIELD_REPOSITORY);
        repoQuery.append(":("); // this is because the lucene query is sad

        Iterator<String> iter = repositoryNames.iterator();
        while (iter.hasNext()) {
            repoQuery.append(iter.next());
            if (iter.hasNext()) {
                repoQuery.append(" OR ");
            }
        }

        repoQuery.append(")");

        return query + repoQuery.toString();
    }

    private void initSearcher() throws InvalidIndexException {
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
