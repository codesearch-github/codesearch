/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
     * determines whether the content of the field will be stored as Field.Index.Analyzed in the lucene index
     * @return true is it is analyzed
     */
    public abstract boolean isAnalyzed();

    /**
     * determines whether the content of the field should also be stored in another field as lowercase
     * if true the IndexingTask will create an additional field with the _lc at the end storing the content of the field in lowercase
     * @return 
     */
    public abstract boolean addLowercase();

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
     * returns the analyzer used for this field in regular case
     * @return
     */
    public abstract Analyzer getRegularCaseAnalyzer();

    /**
     * returns the analyzer used for the content of the lower case field
     * only used if addLowercase() returns true
     * @return
     */
    public abstract Analyzer getLowerCaseAnalyzer();

    /** {@inheritDoc} */
    @Override
    public String getPurposes(){
        return "lucene_field_plugin";
    }

    /** {@inheritDoc} */
    @Override
    public String getVersion() {
        return "1.0";
    }

}
