/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.indexer.tasks;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;
import org.codesearch.commons.plugins.vcs.VersionControlPluginException;

/**
 * This class is the base class used for indexing
 *
 * @author zeheron
 */
public abstract class IndexingTask implements Task {

    /** The IndexingTask to be processed */
    protected Set<String> filesNames;
    /* Instantiate a logger  */
    protected static final Logger log = Logger.getLogger(IndexingTask.class);
    /** The currently active IndexWriter */
    protected IndexWriter indexWriter;
    /** The index directory, contains all index files */
    protected FSDirectory indexDirectory;
    /** The Version control Plugin */
    protected VersionControlPlugin vcp;

    @Override
    public abstract void execute();

    /**
     * Initializes the version control plugin
     * @throws Exception
     */
    public abstract void initializeVersionControlPlugin();

    /**
     * Adds the Fields to the lucene document.
     * @param doc the target document
     * @return document with added lucene fields
     */
    public Document addLuceneFields(Document doc, String path) throws VersionControlPluginException {
        doc.add(new Field("TITLE", extractFilename(path), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("CONTENT", vcp.getFileContentForFilePath(path), Field.Store.YES, Field.Index.ANALYZED));
        return doc;
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
            log.error(ex);
            log.error("IndexWriter initialization error: Could not open directory " + dir.getAbsolutePath());
        }

    }

    /**
     * Creates an index for the given Directory.
     */
    public boolean createIndex() {
        if (indexWriter == null) {
            log.error("Creation of indexDirectory failed due to missing initialization of IndexWriter!");
            return false;
        }
        Document doc = new Document();
        try {
            Iterator it = filesNames.iterator();
            while (it.hasNext()) {
                String path = (String) it.next();
                // The lucene document containing all relevant indexing information
                doc = new Document();
                // Add fields
                doc = addLuceneFields(doc, path);
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
            log.error("Adding file to index: " + doc.get("title") + " failed! \n" + ex.getMessage());
        } catch (NullPointerException ex) {
            log.error("NullPointerException: FileContentDirectory is empty!" + ex.getMessage());
        }
        return true;
    }

    /**
     * Extracts the filename out of the given path
     * @param path - a filepath
     * @return the name of the file
     */
    public String extractFilename(final String path) {
        String[] parts = path.split("/");
        return parts[parts.length];
    }
}
