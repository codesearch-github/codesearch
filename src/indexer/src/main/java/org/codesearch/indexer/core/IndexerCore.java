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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.indexer.core;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.codesearch.utils.IndexLogger;
import org.codesearch.utils.FileTool;
import org.codesearch.utils.constants.IndexerConstants;

/**
 * This class can be used to index a repository
 * by specifying the configuration and the
 * directory needed.
 * 
 * @author zeheron
 */
public class IndexerCore {
    
    /** The files to be processed */
    private File[] contentFiles;
    /** The used logger  */
    IndexLogger iLog = new IndexLogger("/tmp/indexerLog.log");   //TODO: Replace test path
    /** The currently active IndexWriter */
    IndexWriter indexWriter;

    /** The index directory, contains all index files */
    FSDirectory indexDirectory;

    /** Default constructor */
    public IndexerCore() {
    }

    /**
     * Deletes the specified index directory physically
     * @param dir the directory to clean
     * @return true if directory is cleaned
     */
    public boolean cleanIndexDirectory(File dir)
    {
        if(FileTool.deleteDirectory(dir))
        {
            iLog.append("Index destoyed sucessfully; " + dir.getAbsolutePath());
            return true;
        }
        else
        {
            iLog.append("Failed to clear IndexDirectory: " + dir.getAbsolutePath());
            return false;
        }
    }

    /**
     * Initializes a IndexWriter with the given analyzer and for the given directory
     * @param analyzer analyzer for the files.
     * @param dir The location for the  lucene index
     */
    public void initializeIndexWriter(Analyzer analyzer, File dir) {
        try {
            Analyzer luceneAnalyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
            indexDirectory = FSDirectory.open(dir);
            indexWriter = new IndexWriter(indexDirectory, luceneAnalyzer, IndexWriter.MaxFieldLength.LIMITED);
            iLog.append("IndexWriter initilaization succesful: " + dir.getAbsolutePath());
        } catch (IOException ex) {
            IndexLogger.log.error(ex);
            iLog.append("IndexWriter initialization error: Could not open directory " + dir.getAbsolutePath() );
        }

    }

    /**
     * Creates an index for the given Directory.
     *
     * @param dir directory to be indexed
     */
    public boolean indexDirectory(File dir) { //TODO Answer question from David: this method does not index recursively, does it? so only the files that are directly in the specified folder will be indexed and not if they are in a subfolder
        if(indexWriter == null)
        {
            iLog.append("Creation of indexDirectory failed due to missing initialization of IndexWriter!");
            return false;
        }
        contentFiles = dir.listFiles();
        Document doc = new Document();
        try {
            for (int i = 0; i < contentFiles.length; i++) {
                File currentFile = contentFiles[i];
                // The lucene document containing all relevant indexing information
                doc = new Document();
                // Add fields
                doc.add(new Field("title", currentFile.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
                doc.add(new Field("content", FileTool.readTextFile(currentFile), Field.Store.YES, Field.Index.ANALYZED));
                iLog.append("Added file: " + doc.get("title") + " to index.");
                // Add document to the index
                indexWriter.addDocument(doc);
            }
            indexWriter.optimize();
            indexWriter.close();
            iLog.append("Index creation sucessful: " + doc.getField("title"));
        } catch (CorruptIndexException ex) {
            iLog.append("Indexing  of: " + doc.get("title") + " failed! \n");
        } catch (IOException ex) {
            iLog.append("Adding file to index: " + doc.get("title") + " failed! \n");
        } catch(NullPointerException ex){
             iLog.append("NullPointerException: FileContentDirectory is empty!");
        }
        return true;
    }


    public File[] getContentFiles() {
        return contentFiles;
    }

    public IndexLogger getiLog() {
        return iLog;
    }

    public FSDirectory getIndexDirectory() {
        return indexDirectory;
    }

    public IndexWriter getIndexWriter() {
        return indexWriter;
    }

    
}
