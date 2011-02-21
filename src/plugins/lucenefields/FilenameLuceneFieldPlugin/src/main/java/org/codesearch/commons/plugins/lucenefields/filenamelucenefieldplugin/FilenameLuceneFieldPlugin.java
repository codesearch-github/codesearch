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
