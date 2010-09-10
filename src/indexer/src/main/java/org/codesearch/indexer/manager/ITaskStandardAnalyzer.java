/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.indexer.manager;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.util.Version;
import org.codesearch.commons.constants.IndexConstants;
import org.codesearch.indexer.core.IndexerCore;
import org.codesearch.utils.FileTool;


/**
 *
 * @author zeheron
 */
public class ITaskStandardAnalyzer implements ITask {

    /** Directory including the files to be indexed*/
    File pathToFileDir;
    /** Target Directory for the index files */
    File pathToIndexDir;
    /** Just a standard lucene Analyzer */
    Analyzer luceneAnalyzer;

    /**
     * Creates an indexing Task using a default Analyzer
     * @param pathToIndexDir  Target Directory for the index files
     * @param pathToFileDir Directory including the files to be indexed  
     */
    public ITaskStandardAnalyzer(String pathToIndexDir, String pathToFileDir)
    {
        this.pathToFileDir  = new File(pathToFileDir);
        this.pathToIndexDir = new File(pathToIndexDir);
        luceneAnalyzer = new StandardAnalyzer(IndexConstants.LUCENE_VERSION);
    }

    @Override
    public boolean execute(Thread parentThread) {
        IndexerCore ic = new IndexerCore();
        ic.initializeIndexWriter(luceneAnalyzer, pathToIndexDir);
        return ic.indexDirectory(pathToFileDir);
    }

    @Override
    public boolean revertChanges() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
