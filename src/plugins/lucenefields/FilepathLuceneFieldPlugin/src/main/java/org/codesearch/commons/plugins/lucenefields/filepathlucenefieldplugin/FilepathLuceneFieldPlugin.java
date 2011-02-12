/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.lucenefields.filepathlucenefieldplugin;

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
    public String getFieldValue(FileDto fileDto) throws LuceneFieldValueException {
        return fileDto.getFilePath();
    }

    /** {@inheritDoc} */
    public String getPurposes() {
        return "lucene_field_plugin";
    }

    /**
     * returns true
     * @return
     */
    public boolean isAnalyzed() {
        return true;
    }

    /**
     * returns true
     * @return
     */
    public boolean isStored() {
        return true;
    }

    /** {@inheritDoc} */
    public boolean addLowercase() {
        return true;
    }

    public String getFieldName() {
        return "filepath";
    }

}
