/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.searcher.server.util;

import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter.Side;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

/**
 * A custom analyzer used for autocompletion
 * @author zeheron
 */
public class STAutocompleteLuceneAnalyzer extends Analyzer {

    /** {@inheritDoc} */
    @Override
    public TokenStream tokenStream(String string, Reader reader) {
        TokenStream result = new StandardTokenizer(Version.LUCENE_30, reader);
        result = new StandardFilter(result);
        result = new LowerCaseFilter(result);
        result = new EdgeNGramTokenFilter(
                result, Side.FRONT, 1, 20);

        return result;
    }
}
