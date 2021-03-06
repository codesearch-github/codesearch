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
package org.codesearch.commons.plugins.lucenefields.core;

import org.apache.lucene.analysis.Analyzer;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldPlugin;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldValueException;
import org.codesearch.commons.plugins.lucenefields.SimpleSourceCodeAnalyzer;
import org.codesearch.commons.plugins.vcs.FileDto;
import org.codesearch.commons.utils.mime.MimeTypeUtil;

/**
 *
 * @author David Froehlich
 */
public class ContentLuceneFieldPlugin implements LuceneFieldPlugin {

    /**
     * checks whether the file is flagged as binary
     * returns the content of the fileDto for non binary files and an empty String for binary files
     * @param fileDto
     * @return
     * @throws LuceneFieldValueException
     */
    @Override
    public String getFieldValue(FileDto fileDto) throws LuceneFieldValueException {
        String content = "";
        try{if (!fileDto.isBinary() && !MimeTypeUtil.isBinaryType(MimeTypeUtil.guessMimeTypeViaFileEnding(fileDto.getFilePath().substring(fileDto.getFilePath().lastIndexOf('.'))))) {
            content = new String(fileDto.getContent());
        }
        } catch(StringIndexOutOfBoundsException ex){
            //in case the file does not have a file ending it is assumed that it is binary, therefore the content will not be stored in the index
        }
        return content;
    }

    /** {@inheritDoc} */
    @Override
    public String getPurposes() {
        return getFieldName();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isStored() {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public String getFieldName() {
        return "content";
    }

    /** {@inheritDoc} */
    @Override
    public Analyzer getRegularCaseAnalyzer() {
        return new SimpleSourceCodeAnalyzer(true);
    }

    /** {@inheritDoc} */
    @Override
    public Analyzer getLowerCaseAnalyzer() {
        return new SimpleSourceCodeAnalyzer(false);
    }

    /**
     * Returns an alias for the field name to reduce the amount of typing the user has to do
     * returns the same as getFieldName since it is the default field, so the name isn't typed anyways
     */
    @Override
    public String getAbbreviatedFieldName() {
        return "content";
    }
}
