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
import org.codesearch.commons.plugins.PluginBase;
import org.codesearch.commons.plugins.vcs.FileDto;

/**
 * Plugin that adds a search field to the index.
 */
@PluginBase
public interface LuceneFieldPlugin extends Plugin {

    /**
     * Returns the value that is to be added to the field in the index for the given file.
     * @param fileDto 
     * @return the content of the field
     * @throws LuceneFieldValueException
     */
    String getFieldValue(FileDto fileDto) throws LuceneFieldValueException;

    /**
     * Whether values of the field should be stored in the index so the
     * original value can be retrieved.
     */
    boolean isStored();

    /**
     * Returns the name of the field which will be seen from and usable from
     * the search interface.
     */
    String getFieldName();

    /**
     * Returns an alias for the field name to reduce the amount of typing the user has to do
     */
    String getAbbreviatedFieldName();
    /**
     * Returns the analyzer used for this field in regular case.
     * If the field should not be analyzed, return null.
     */
    Analyzer getRegularCaseAnalyzer();

    /**
     * Returns the analyzer used for the content of the lower case field.
     * If the field should not be analyzed in lower case, return null.
     */
    Analyzer getLowerCaseAnalyzer();
}
