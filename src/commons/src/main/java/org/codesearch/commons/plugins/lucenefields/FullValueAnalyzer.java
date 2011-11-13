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
 * Analyzes the field as a whole word, the term is never split
 * @author David Froehlich
 * @author Samuel Kogler
 */
public class FullValueAnalyzer extends Analyzer 
{
    /** whether or not the output should be parsed to lower case */
    private boolean caseSensitive;
    
    /**
     * creates a new instance of FullValueAnalyzer
     * @param caseSensitive uses a FullValueTokenizer if false and a FullValueLowercaseTokenizer if true
     */
    public FullValueAnalyzer(boolean caseSensitive){
        this.caseSensitive = caseSensitive;
    }
    
    /** {@inheritDoc} */
    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
        if(caseSensitive){
            return new FullValueTokenizer(IndexConstants.LUCENE_VERSION, reader);
        } else {
            return new FullValueLowercaseTokenizer(IndexConstants.LUCENE_VERSION, reader);
        }
    }
    
    /**
     * Tokenizer that never splits the term.
     */
    class FullValueTokenizer extends CharTokenizer {
        public FullValueTokenizer(Version matchVersion, Reader input) {
            super(matchVersion, input);
        }

        @Override
        protected boolean isTokenChar(int c) {
            return false;
        }
    }
    
    /**
     * Tokenizer that never splits the term but converts the input to lowercase.
     */
    class FullValueLowercaseTokenizer extends FullValueTokenizer {

        public FullValueLowercaseTokenizer(Version matchVersion, Reader input) {
            super(matchVersion, input);
        }
        
        @Override
        protected int normalize(int c) {
            return Character.toLowerCase(c);
        }
    }
}
