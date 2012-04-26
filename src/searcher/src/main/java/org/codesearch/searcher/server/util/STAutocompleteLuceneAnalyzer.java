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
import org.codesearch.commons.constants.IndexConstants;

/**
 * A custom analyzer used for autocompletion
 * @author Stephan Stiboller
 */
@Deprecated
public final class STAutocompleteLuceneAnalyzer extends Analyzer {

    /** {@inheritDoc} */
    @Override
    public TokenStream tokenStream(String string, Reader reader) {
        TokenStream result = new StandardTokenizer(IndexConstants.LUCENE_VERSION, reader);
        result = new StandardFilter(IndexConstants.LUCENE_VERSION, result);
        result = new LowerCaseFilter(IndexConstants.LUCENE_VERSION, result);
        result = new EdgeNGramTokenFilter(
                result, Side.FRONT, 1, 20);

        return result;
    }
}
