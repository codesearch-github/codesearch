package org.codesearch.commons.plugins.lucenefields.repositorygroupucenefieldplugin;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.WhitespaceTokenizer;
import org.codesearch.commons.constants.IndexConstants;

/**
 * Case insensitive version of {@link WhitespaceAnalyzer}.
 * @author Samuel Kogler
 */
public class LowerCaseWhitespaceAnalyzer extends Analyzer {
    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
        return new LowerCaseFilter(IndexConstants.LUCENE_VERSION, new WhitespaceTokenizer(IndexConstants.LUCENE_VERSION, reader));
    }
}
