/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.lucenefields.contentlucenefieldplugin;

import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LetterTokenizer;
import org.apache.lucene.analysis.TokenStream;

/**
 *
 * @author David Froehlich
 */
public class LetterAnalyzer extends Analyzer {

    /** {@inheritDoc} */
    @Override
    public TokenStream tokenStream(final String fieldName, final Reader reader) {
        return new LetterTokenizer(reader);
    }
}