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

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.lucene.store.LockObtainFailedException;
import org.codesearch.indexer.exceptions.TaskExecutionException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.codesearch.commons.configuration.properties.PropertiesManager;
import org.codesearch.commons.configuration.xml.XmlConfigurationReader;
import org.codesearch.commons.configuration.xml.XmlConfigurationReaderConstants;
import org.codesearch.commons.constants.IndexConstants;
import org.codesearch.commons.plugins.PluginLoader;
import org.codesearch.commons.plugins.PluginLoaderException;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;
import org.codesearch.commons.plugins.vcs.VersionControlPluginException;
import org.springframework.beans.factory.annotation.Autowired;

//FIXME index is opened and closed for !!every single!! repository
//TODO Research on IndexWriter.updateDocument
/**
 * This task performs basic indexing of one repository.
 * The indexLocation and the repository must be set before executing this task.
 *
 * @author Stephan Stiboller
 * @author David Froehlich
 */
public class IndexingTask extends Task {

    /* Instantiate a logger  */
    private static final Logger LOG = Logger.getLogger(IndexingTask.class);
    /** The IndexingTask to be processed */
    private Set<String> fileNames;
    /** The currently active IndexWriter */
    private IndexWriter indexWriter;
    /** The indexReader used to delete documents */
    private IndexReader indexReader;
    /** The index directory, contains all index files */
    private FSDirectory indexDirectory;
    /** The Version control Plugin */
    private VersionControlPlugin versionControlPlugin;
    /** The used PropertyReader */
    private PropertiesManager propertiesReader;
    /** The plugin loader. */
    private PluginLoader pluginLoader;

    /**
     * executes the task,
     * updates the index fields of the set repository
     * @throws TaskExecutionException
     */
    @Override
    public void execute() throws TaskExecutionException {
        if (indexLocation == null) {
            throw new TaskExecutionException("Index location not set before executing task!");
        }
        if (repository == null) {
            throw new TaskExecutionException("Repository not set before executing indexing task!");
        }
        try {
            LOG.info("Starting execution of indexing task");
            // Read the index status file
            propertiesReader = new PropertiesManager(indexLocation + File.separator + "revisions.properties");
            // Get the version control plugins
            versionControlPlugin = pluginLoader.getPlugin(VersionControlPlugin.class, repository.getVersionControlSystem());
            versionControlPlugin.setRepository(new URI(repository.getUrl()), repository.getUsername(), repository.getPassword());
            fileNames = versionControlPlugin.getPathsForChangedFilesSinceRevision(propertiesReader.getPropertyFileValue(repository.getName()));

            if (fileNames.isEmpty()) {
                LOG.info("Index of repository " + repository.getName() + " is up to date");
                return;
            }

            clearPreviousDocuments();
            initializeIndexWriter();
            createIndex();
            propertiesReader.setPropertyFileValue(repository.getName(), versionControlPlugin.getRepositoryRevision());
        } catch (CorruptIndexException ex) {
            throw new TaskExecutionException("Index is currupt, indexing of repository failed: " + ex);
        } catch (LockObtainFailedException ex) {
            throw new TaskExecutionException("Could not write to index because it is locked: " + ex);
        } catch (PluginLoaderException ex) {
            throw new TaskExecutionException("Task execution failed because PluginLoader threw an exception: " + ex);
        } catch (VersionControlPluginException ex) {
            throw new TaskExecutionException("VersionControlPlugin files could not be retrieved: " + ex);
        } catch (URISyntaxException ex) {
            throw new TaskExecutionException("Repository url could not be parsed to URI: " + ex);
        } catch (FileNotFoundException ex) {
            throw new TaskExecutionException("Index not found at task execution: " + ex);
        } catch (IOException ex) {
            throw new TaskExecutionException("IOException at execution of task: " + ex);
        }
    }

    /**
     * Adds the Fields to the lucene document.
     * @param doc the target document
     * @return document with added lucene fields
     */
    public Document addLuceneFields(Document doc, String path) throws VersionControlPluginException {
        doc.add(new Field(IndexConstants.INDEX_FIELD_TITLE, extractFilename(path), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field(IndexConstants.INDEX_FIELD_FILEPATH, path, Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field(IndexConstants.INDEX_FIELD_CONTENT, versionControlPlugin.getFileContentForFilePath(path), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field(IndexConstants.INDEX_FIELD_REPOSITORY, repository.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field(IndexConstants.INDEX_FIELD_REVISION, versionControlPlugin.getRepositoryRevision(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field(IndexConstants.INDEX_FILED_REPOSITORY_GROUP, repository.getRepositoryGroupsAsString(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field(IndexConstants.INDEX_FIELD_TITLE_LC, extractFilename(path).toLowerCase(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field(IndexConstants.INDEX_FIELD_FILEPATH_LC, path.toLowerCase(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field(IndexConstants.INDEX_FIELD_CONTENT_LC, versionControlPlugin.getFileContentForFilePath(path).toLowerCase(), Field.Store.YES, Field.Index.ANALYZED));

        return doc;
    }

    /**
     * Initializes the IndexWriter
     */
    private void initializeIndexWriter() throws CorruptIndexException, LockObtainFailedException, IOException {
        indexDirectory = FSDirectory.open(new File(indexLocation));
        indexWriter = new IndexWriter(indexDirectory, new WhitespaceAnalyzer(), IndexWriter.MaxFieldLength.LIMITED);
        LOG.debug("IndexWriter initialization successful for directory: " + indexDirectory.getFile().getAbsolutePath());
    }

    /**
     * Creates an index for the current repository.
     */
    private boolean createIndex() throws VersionControlPluginException, CorruptIndexException, IOException {
        if (indexWriter == null) {
            LOG.error("Creation of indexDirectory failed due to missing initialization of IndexWriter!");
            return false;
        }
        Document doc = null;
        for (String path : fileNames) {
            if (!(fileIsOnIgnoreList(path))) {
                // The lucene document containing all relevant indexing information
                doc = new Document();
                // Add fields
                doc = addLuceneFields(doc, path);
                // Add document to the index
                indexWriter.addDocument(doc);
                LOG.debug("Added file: " + doc.get(IndexConstants.INDEX_FIELD_TITLE) + " to index.");
            }
        }
        indexWriter.commit();
        //FIXME do not always optimize index, configure a max amount of indexing operations before optimizing
        //indexWriter.optimize();
        indexWriter.close();
        return true;
    }

    /**
     * Removes all documents from the index that will be replaced by updated documents.
     */
    private void clearPreviousDocuments() throws CorruptIndexException, IOException {
        if (indexWriter == null) {
            LOG.error("Clearing of Documents failed due to missing initialization of IndexWriter!");
        }
        IndexSearcher searcher = null;
        try {
            searcher = new IndexSearcher(indexDirectory, false);
        } catch (IOException ex) {
            //In case no index was found no documents have to be deleted
            return;
        }
        indexReader = searcher.getIndexReader();
        for (String path : fileNames) {
            Term term = new Term(IndexConstants.INDEX_FIELD_FILEPATH, path);
            indexReader.deleteDocuments(term);
        }
        indexReader.close();
    }

    /**
     * Checks whether the given file is on the list of files that will not be indexed
     */
    public boolean fileIsOnIgnoreList(String path) {
        Pattern p;
        for (String s : repository.getIgnoredFileNames()) {
            p = Pattern.compile(parseRegexString(s));
            Matcher m = p.matcher(path);
            if (m.find()) {
                return true;
            }
        }
        return false;
    }

    //TODO check for different chars the user could specify in configuration
    //TODO research whether there is a library for wildcard stuff
    /**
     * Parses the string that represents an entry in the ignore list from the configuration to a string that can be read by java regex
     * @param string the string that is to be parsed
     * @return the regex pattern string
     */
    private String parseRegexString(String string) {
        String retString = string.replace(".", "\\.").replace("*", ".*");
        return retString;
    }

    /**
     * Extracts the filename out of the given path
     * @param path - a file path
     * @return the name of the file
     */
    public String extractFilename(final String path) {
        return path.substring(path.lastIndexOf('/') + 1);
    }

    public void setPluginLoader(PluginLoader pluginLoader) {
        this.pluginLoader = pluginLoader;
    }
}
