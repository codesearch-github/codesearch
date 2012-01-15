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

package org.codesearch.commons.plugins.lucenefields.repositorygroupucenefieldplugin;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.codesearch.commons.constants.IndexConstants;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldPlugin;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldValueException;
import org.codesearch.commons.plugins.vcs.FileDto;

/**
 * Plugin that adds repository groups to the index.
 * @author Samuel Kogler
 */
public class RepositoryGroupLuceneFieldPlugin extends LuceneFieldPlugin {

    /** Analyzer used for case sensitive queries. */
    private Analyzer regularCaseAnalyzer = new WhitespaceAnalyzer(IndexConstants.LUCENE_VERSION);
    /** Analyzer used for case insensitive queries. */
    private Analyzer lowerCaseAnalyzer = new LowerCaseWhitespaceAnalyzer();

    /** {@inheritDoc} */
    @Override
    public String getFieldValue(FileDto fileDto) throws LuceneFieldValueException {
        return fileDto.getRepository().getRepositoryGroupsAsString();
    }

    /** {@inheritDoc} */
    @Override
    public String getPurposes() {
        return "lucene_field_plugin";
    }

    /** {@inheritDoc} */
    @Override
    public boolean isStored() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public String getFieldName() {
        return "group";
    }

    /** {@inheritDoc} */
    @Override
    public Analyzer getRegularCaseAnalyzer() {
        return regularCaseAnalyzer;
    }

    /** {@inheritDoc} */
    @Override
    public Analyzer getLowerCaseAnalyzer() {
        return lowerCaseAnalyzer;
    }
}
