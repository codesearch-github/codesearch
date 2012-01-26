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
package org.codesearch.commons.plugins.lucenefields;

import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.Version;
import org.codesearch.commons.constants.IndexConstants;

/**
 *
 * @author David Froehlich
 */
public final class SimpleSourceCodeAnalyzer extends Analyzer {
    private boolean caseSensitive;

    public SimpleSourceCodeAnalyzer(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    /** {@inheritDoc} */
    @Override
    public TokenStream tokenStream(final String fieldName, final Reader reader) {
        if(caseSensitive){
            return new SimpleSourceCodeTokenizer(IndexConstants.LUCENE_VERSION, reader);
        }
        return new SimpleLowercaseSourceCodeTokenizer(IndexConstants.LUCENE_VERSION, reader);
    }

    class SimpleSourceCodeTokenizer extends CharTokenizer {

        SimpleSourceCodeTokenizer(Version matchVersion, Reader in) {
            super(matchVersion, in);
        }

        @Override
        protected boolean isTokenChar(int c) {
            return Character.isJavaIdentifierPart(c) || c == '-' || c == '@' || c == '+' || c == '*' || c == '/' || c == '%';
        }
    }

    class SimpleLowercaseSourceCodeTokenizer extends SimpleSourceCodeTokenizer {

        SimpleLowercaseSourceCodeTokenizer(Version matchVersion, Reader in) {
            super(matchVersion, in);
        }

        @Override
        protected int normalize(int c) {
            return Character.toLowerCase(c);
        }
    }
}