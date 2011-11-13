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

import org.apache.lucene.analysis.Analyzer;
import org.codesearch.commons.plugins.Plugin;
import org.codesearch.commons.plugins.vcs.FileDto;

/**
 *
 * @author David Froehlich
 */
public abstract class LuceneFieldPlugin implements Plugin {

    /**
     * returns the content that will be written in the field of the lucene index
     * the field is identified by the String returned by the purpose
     * @param fileDto the FileDto where the data will be extracted
     * @return the content of the field
     * @throws LuceneFieldValueException
     */
    public abstract String getFieldValue(FileDto fileDto) throws LuceneFieldValueException;

    /**
     * determines whether the content of the field will be stored as Field.Stored.Yes in the lucene index
     * @return
     */
    public abstract boolean isStored();

    /**
     * returns the name of the field the fieldcontent is gonna be written into
     * @return
     */
    public abstract String getFieldName();

    /**
     * Returns the analyzer used for this field in regular case.
     * If the field should not be analyzed, return null.
     * @return
     */
    public abstract Analyzer getRegularCaseAnalyzer();

    /**
     * Returns the analyzer used for the content of the lower case field.
     * If the field should not be analyzed in lower case, return null.
     * @return
     */
    public abstract Analyzer getLowerCaseAnalyzer();

    /** {@inheritDoc} */
    @Override
    public String getPurposes(){
        return "lucene_field_plugin";
    }
}
