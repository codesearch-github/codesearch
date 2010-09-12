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
package org.codesearch.indexer.tasks;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.codesearch.commons.configreader.xml.dto.RepositoryDto;
import org.codesearch.commons.constants.IndexConstants;
import org.codesearch.commons.plugins.PluginLoader;
import org.codesearch.commons.plugins.PluginLoaderException;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;
import org.codesearch.commons.plugins.vcs.VersionControlPluginException;

/**
 * This class is the base class used for indexing
 *
 * @author Stephan Stiboller
 * @author David Froehlich
 */
public class IndexingTask implements Task {

    private RepositoryDto repository;
    /** The IndexingTask to be processed */
    protected Set<String> fileNames; //TODO probably make private
    /* Instantiate a logger  */
    protected static final Logger log = Logger.getLogger(IndexingTask.class);
    /** The currently active IndexWriter */
    protected IndexWriter indexWriter;
    /** The index directory, contains all index files */
    protected FSDirectory indexDirectory;
    /** The Version control Plugin */
    protected VersionControlPlugin vcp;

    public void setRepository(RepositoryDto repository) {
        this.repository = repository;
    }

    public FSDirectory getIndexDirectory() {
        return indexDirectory;
    }

    public void setIndexDirectory(FSDirectory indexDirectory) {
        this.indexDirectory = indexDirectory;
    }

    @Override
    public void execute() throws TaskExecutionException {
        try {
            initializeVersionControlPlugin();
        } catch (PluginLoaderException ex) {
            throw new TaskExecutionException("VersionControlPlugin could not be loaded: " + ex.getMessage());
        }
        initializeIndexWriter(new StandardAnalyzer(IndexConstants.LUCENE_VERSION), new File(IndexConstants.INDEX_DIRECTORY));
        try {
            fileNames = vcp.getPathsForChangedFilesSinceRevision("0");//TODO read revision number
            this.createIndex();
        } catch (VersionControlPluginException ex) {
            throw new TaskExecutionException("Error with version control plugin: "+ex.getMessage());
        }
    }

    /**
     * Initializes the version control plugin
     * @throws Exception
     */
    public void initializeVersionControlPlugin() throws PluginLoaderException {
        PluginLoader pl = new PluginLoader(VersionControlPlugin.class);
        vcp = (VersionControlPlugin) pl.getPluginForPurpose(repository.getVersionControlSystem());
    }

    /**
     * Adds the Fields to the lucene document.
     * @param doc the target document
     * @return document with added lucene fields
     */
    public Document addLuceneFields(Document doc, String path) throws VersionControlPluginException { //TODO additional fields required
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
            Iterator it = fileNames.iterator();
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
        } catch (VersionControlPluginException ex) { //TODO probably replace log messages with throwing of exceptions
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
