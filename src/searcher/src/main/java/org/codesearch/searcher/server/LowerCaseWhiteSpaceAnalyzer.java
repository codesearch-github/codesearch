/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.searcher.server;

import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceAnalyzer;

/**
 * Extends WhitespaceAnalyzer with LowerCaseFilter.
 * @author Samuel Kogler
 */
public class LowerCaseWhiteSpaceAnalyzer extends Analyzer {

    private static final Analyzer STANDARD = new WhitespaceAnalyzer();

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
        return new LowerCaseFilter(STANDARD.tokenStream(fieldName, reader));
    }
}
