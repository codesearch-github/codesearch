package org.codesearch.searcher.server.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author zeheron
 */
public final class STAutocompleter {

    private static final String GRAMMED_WORDS_FIELD = "words";
    private static final String SOURCE_WORD_FIELD = "sourceWord";
    private final Directory autoCompleteDirectory;
    private IndexReader autoCompleteReader;
    private IndexSearcher autoCompleteSearcher;

    public STAutocompleter(String autoCompleteDir) throws IOException {
        this.autoCompleteDirectory = FSDirectory.open(new File(autoCompleteDir));
        setupReader();
    }

    public List<String> suggest(String term) throws IOException {
        Query query = new TermQuery(new Term(GRAMMED_WORDS_FIELD, term));
        TopDocs docs = autoCompleteSearcher.search(query, 50);
        List<String> suggestions = new ArrayList<String>();
        for (ScoreDoc doc : docs.scoreDocs) {
            suggestions.add(autoCompleteReader.document(doc.doc).get(
                    SOURCE_WORD_FIELD));
        }
        return suggestions;
    }

    public void setupIndex(Directory sourceDirectory, String fieldToAutocomplete) throws CorruptIndexException, IOException {
        IndexReader sourceReader = IndexReader.open(sourceDirectory);
        LuceneDictionary dict = new LuceneDictionary(sourceReader, fieldToAutocomplete);
        IndexWriter writer = new IndexWriter(autoCompleteDirectory, new STAutocompleteLuceneAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
        writer.setMergeFactor(300);
        writer.setMaxBufferedDocs(150);
        Map<String, Integer> wordsMap = new HashMap<String, Integer>();
        Iterator<String> iter = (Iterator<String>) dict.getWordsIterator();
        while (iter.hasNext()) {
            String word = iter.next();
            if (word.length() < 0) {
                continue;
            }
            wordsMap.put(word, sourceReader.docFreq(new Term(fieldToAutocomplete, word)));
        }
        System.out.println("SetupIndex: " + GRAMMED_WORDS_FIELD);
        for (String word : wordsMap.keySet()) {
            Document doc = new Document();
            doc.add(new Field(SOURCE_WORD_FIELD, word, Field.Store.YES, Field.Index.NOT_ANALYZED));
            System.out.println(":s"+word);
            doc.add(new Field(GRAMMED_WORDS_FIELD, word, Field.Store.YES, Field.Index.ANALYZED));
            System.out.println(":g"+word);
            writer.addDocument(doc);
        }
        sourceReader.close();
        writer.optimize();
        writer.close();
        setupReader();
    }

    private void setupReader() throws CorruptIndexException, IOException {
        if (autoCompleteReader == null) {
            autoCompleteReader = IndexReader.open(autoCompleteDirectory);
        } else {
            autoCompleteReader.reopen();
        }
        autoCompleteSearcher = new IndexSearcher(autoCompleteReader);
    }
}
