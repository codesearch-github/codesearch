/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.lucenefields.revisionluceneplugin;

import org.apache.lucene.analysis.Analyzer;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldPlugin;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldValueException;
import org.codesearch.commons.plugins.vcs.FileDto;

/**
 *
 * @author David Froehlich
 */
public class RevisionLuceneFieldPlugin extends LuceneFieldPlugin {

    /**  {@inheritDoc} */
    @Override
    public String getFieldValue(FileDto fileDto) throws LuceneFieldValueException {
        return fileDto.getLastAlteration();
    }

    /**  {@inheritDoc} */
    @Override
    public boolean isAnalyzed() {
        return false;
    }

    /**  {@inheritDoc} */
    @Override
    public boolean addLowercase() {
        return false;
    }

    /**  {@inheritDoc} */
    @Override
    public boolean isStored() {
        return false;
    }

    /**  {@inheritDoc} */
    @Override
    public String getFieldName() {
        return "revision";
    }

    /**  {@inheritDoc} */
    @Override
    public Analyzer getRegularCaseAnalyzer() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Analyzer getLowerCaseAnalyzer() {
        return null;
    }
}
