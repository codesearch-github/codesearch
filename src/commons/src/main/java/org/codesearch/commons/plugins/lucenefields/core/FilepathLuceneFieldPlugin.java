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
package org.codesearch.commons.plugins.lucenefields.core;
import org.apache.lucene.analysis.Analyzer;
import org.codesearch.commons.plugins.lucenefields.FullValueAnalyzer;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldPlugin;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldValueException;
import org.codesearch.commons.plugins.vcs.FileDto;

/**
 *
 * @author David Froehlich
 */
public class FilepathLuceneFieldPlugin extends LuceneFieldPlugin {

    /**
     * returns the filepath of the fileDto
     * @param fileDto
     * @return
     * @throws LuceneFieldValueException
     */
    @Override
    public String getFieldValue(FileDto fileDto) throws LuceneFieldValueException {
        return fileDto.getFilePath();
    }

    /** {@inheritDoc} */
    @Override
    public String getPurposes() {
        return "lucene_field_plugin";
    }

    /** {@inheritDoc} */
    @Override
    public boolean isStored() {
        return true;
    }

    @Override
    /** {@inheritDoc} */
    public String getFieldName() {
        return "filepath";
    }

    @Override
    /** {@inheritDoc} */
    public Analyzer getRegularCaseAnalyzer() {
        return new FullValueAnalyzer(true);
    }

    @Override
    /** {@inheritDoc} */
    public Analyzer getLowerCaseAnalyzer() {
        return new FullValueAnalyzer(false);
    }
}
