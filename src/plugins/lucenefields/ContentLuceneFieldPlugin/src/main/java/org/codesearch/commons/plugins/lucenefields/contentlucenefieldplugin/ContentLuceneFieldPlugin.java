/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.lucenefields.contentlucenefieldplugin;

import org.codesearch.commons.plugins.lucenefields.LuceneFieldPlugin;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldValueException;
import org.codesearch.commons.plugins.vcs.FileDto;
import org.codesearch.commons.utils.MimeTypeUtil;

/**
 *
 * @author David Froehlich
 */
public class ContentLuceneFieldPlugin extends LuceneFieldPlugin {

    /**
     * checks whether the file is flagged as binary
     * returns the content of the fileDto for non binary files and an empty String for binary files
     * @param fileDto
     * @return
     * @throws LuceneFieldValueException
     */
    public String getFieldValue(FileDto fileDto) throws LuceneFieldValueException {
        String content = "";
        try{if (!fileDto.isBinary() && !MimeTypeUtil.isBinaryType(MimeTypeUtil.guessMimeTypeViaFileEnding(fileDto.getFilePath().substring(fileDto.getFilePath().lastIndexOf("."))))) {
            content = new String(fileDto.getContent());
        }
        } catch(StringIndexOutOfBoundsException ex){
            //in case the file does not have a file ending it is assumed that it is binary, therefore the content will not be stored in the index
        }
        return content;
    }

    /** {@inheritDoc} */
    public String getPurposes() {
        return "lucene_field_plugin";
    }

    /** {@inheritDoc} */
    public boolean isAnalyzed() {
        return true;
    }

    /** {@inheritDoc} */
    public boolean isStored() {
        return true;
    }

    /** {@inheritDoc} */
    public boolean addLowercase() {
        return true;
    }

    public String getFieldName() {
        return "content";
    }
}
