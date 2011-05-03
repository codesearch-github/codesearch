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
package org.codesearch.commons.plugins.lucenefields.filenamelucenefieldplugin;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldPlugin;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldValueException;
import org.codesearch.commons.plugins.lucenefields.contentlucenefieldplugin.LetterAnalyzer;
import org.codesearch.commons.plugins.vcs.FileDto;

/**
 *
 * @author David Froehlich
 */
public class FilenameLuceneFieldPlugin extends LuceneFieldPlugin {
    /** {@inhertDoc} */
    public String getFieldValue(FileDto fileDto) throws LuceneFieldValueException {
        try {
            return fileDto.getFilePath().substring(fileDto.getFilePath().lastIndexOf("/"));
        } catch (StringIndexOutOfBoundsException ex) {
            return fileDto.getFilePath();
        }

    }

    /** {@inhertDoc} */
    public boolean isAnalyzed() {
        return true;
    }

    /** {@inhertDoc} */
    public boolean addLowercase() {
        return true;
    }

    /** {@inhertDoc} */
    public boolean isStored() {
        return true;
    }

    /** {@inhertDoc} */
    public String getFieldName() {
        return "filename";
    }

    public Analyzer getRegularCaseAnalyzer() {
        return new LetterAnalyzer();
    }

    public Analyzer getLowerCaseAnalyzer() {
        return new SimpleAnalyzer();
    }
}
