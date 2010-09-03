
package org.codesearch.searcher.server;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.Weight;
import org.apache.lucene.store.Directory;
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
    public static final String INDEX_LOCATION_KEY="indexLocation";
    private QueryParser parser;
    private IndexSearcher searcher;
    private PropertyManager propertyM;
    private String indexLocation;

    public DocumentSearcher() throws ConfigurationException, IOException {
        propertyM = new PropertyManager(); //TODO replace with spring injection
        indexLocation = propertyM.getSingleLinePropertyValue(INDEX_LOCATION_KEY);
        parser = new QueryParser(Version.LUCENE_30, "", new StandardAnalyzer(Version.LUCENE_30));
        searcher = new IndexSearcher(FSDirectory.open(new File(indexLocation)), true);
    }

    private Query parseQueryString(String query) throws ParseException{
        Query parsedQuery = parser.parse(query);
        return parsedQuery;
    }

    public List<ResultItem> getResultsForSearch(String searchString) throws ParseException, IOException{
        LinkedList<ResultItem> results = new LinkedList<ResultItem>();
        Query query = parseQueryString(searchString);
        TopDocs topDocs = searcher.search(query, 10000);
        Document doc;
        for(ScoreDoc sd : topDocs.scoreDocs){
            doc = searcher.doc(sd.doc);
            String repo = doc.get(IndexConstants.INDEX_FIELD_REPOSITORY);
            String filePath = doc.get(IndexConstants.INDEX_FIELD_FILEPATH);
            float relevance = sd.score;
            results.add(new ResultItem(filePath, repo, relevance));
            //TODO add relevance
        }
        return results;
    }
}
