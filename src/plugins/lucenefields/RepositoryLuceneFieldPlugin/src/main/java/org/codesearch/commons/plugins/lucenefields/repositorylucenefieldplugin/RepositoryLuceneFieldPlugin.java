/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.lucenefields.repositorylucenefieldplugin;

import org.apache.lucene.analysis.Analyzer;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldPlugin;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldValueException;
import org.codesearch.commons.plugins.vcs.FileDto;

/**
 * LuceneFieldPlugin that returns the name of the repository 
 * @author David Froehlich
 */
public class RepositoryLuceneFieldPlugin extends LuceneFieldPlugin {

    /**
     * returns the name of the repository
     * @param fileDto
     * @return
     * @throws LuceneFieldValueException
     */
    public String getFieldValue(FileDto fileDto) throws LuceneFieldValueException {
        return fileDto.getRepository().getName();
    }

    /** {@inheritDoc} */
    public String getPurposes() {
        return "lucene_field_plugin";
    }

    /**
     * returns false
     * @return
     */
    public boolean isAnalyzed() {
        return false;
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
        return false;
    }

    /** {@inheritDoc} */
    public String getFieldName() {
        return "repository";
    }

    /** {@inheritDoc} */
    public Analyzer getRegularCaseAnalyzer() {
        return null;
    }

    /** {@inheritDoc} */
    public Analyzer getLowerCaseAnalyzer() {
        return null;
    }
}
