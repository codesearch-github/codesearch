/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.indexer.manager;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.codesearch.commons.constants.IndexConstants;
import org.codesearch.commons.plugins.PluginLoader;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;
import org.codesearch.commons.plugins.vcs.VersionControlPluginException;
import org.codesearch.utils.FileTool;
import org.codesearch.utils.IndexLogger;

/**
 *
 * @author zeheron
 */
public class ITaskIndexing implements ITask {

    /** The filesNames to be processed */
    private Set<String> contentFiles;
    /* Instantiate a logger  */
    private static final Logger log = Logger.getLogger(ITaskIndexing.class);
    /** The currently active IndexWriter */
    private IndexWriter indexWriter;
    /** The index directory, contains all index files */
    private FSDirectory indexDirectory;
    /** The Version controll Plugin */
    private VersionControlPlugin vcp;

    @Override
    public boolean execute(Thread parentThread) {
        try {
            PluginLoader pl = new PluginLoader(VersionControlPlugin.class);
            vcp = (VersionControlPlugin) pl.getPluginForPurpose("SVN");
            contentFiles = vcp.getPathsForChangedFilesSinceRevision("0");  // TODO : REVISION EXTRACTING!
            initializeIndexWriter(new StandardAnalyzer(IndexConstants.LUCENE_VERSION), new File(IndexConstants.INDEX_DIRECTORY));
        } catch (Exception ex) {
            log.error("Task execution failed: " + ex.getMessage());
        }

        return true; //TODO REPLACE
    }

    @Override
    public boolean revertChanges() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Initializes a IndexWriter with the given analyzer and for the given directory
     * @param analyzer analyzer for the files.
     * @param dir The location for the  lucene index
     */
    public void initializeIndexWriter(Analyzer luceneAnalyzer, File dir) {
        try {
            indexDirectory = FSDirectory.open(dir);
            indexWriter = new IndexWriter(indexDirectory, luceneAnalyzer, IndexWriter.MaxFieldLength.LIMITED);
            log.debug("IndexWriter initilaization successful: " + dir.getAbsolutePath());
        } catch (IOException ex) {
            IndexLogger.log.error(ex);
            log.error("IndexWriter initialization error: Could not open directory " + dir.getAbsolutePath() );
        }

    }

    /**
     * Creates an index for the given Directory.
     *
     * @param dir directory to be indexed
     */
    public boolean indexDirectory() {
        if (indexWriter == null) {
            log.error("Creation of indexDirectory failed due to missing initialization of IndexWriter!");
            return false;
        }
        Document doc = new Document();
        try {
            Iterator it = contentFiles.iterator();
            while (it.hasNext()) {

                String file = (String) it.next();
                // The lucene document containing all relevant indexing information
                doc = new Document();
                // Add fields
                doc.add(new Field("TITLE", vcp.getFileNameForFilePath(file), Field.Store.YES, Field.Index.NOT_ANALYZED));
                doc.add(new Field("CONTENT", vcp.getFileContentForFilePath(file), Field.Store.YES, Field.Index.ANALYZED));
                log.debug("Added file: " + doc.get("title") + " to index.");
                // Add document to the index
                indexWriter.addDocument(doc);
            }
            indexWriter.commit();
            indexWriter.optimize();
            indexWriter.close();
            //iLog.append("Index creation sucessful: " + doc.getField("title"));
        } catch (VersionControlPluginException ex) {
            log.error("VersionControl Plugin failed: " + ex.getMessage());
        } catch (CorruptIndexException ex) {
            log.error("Indexing  of: " + doc.get("title") + " failed! \n" + ex.getMessage());
        } catch (IOException ex) {
            log.error("Adding file to index: " + doc.get("title") + " failed! \n"+ ex.getMessage());
        } catch (NullPointerException ex) {
            log.error("NullPointerException: FileContentDirectory is empty!"+ ex.getMessage());
        }
        return true;
    }
}
