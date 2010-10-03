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
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.constants.IndexConstants;
import org.codesearch.commons.plugins.PluginLoader;
import org.codesearch.commons.plugins.PluginLoaderException;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;
import org.codesearch.commons.plugins.vcs.VersionControlPluginException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This task performs basic indexing of one repository.
 *
 * @author Stephan Stiboller
 * @author David Froehlich
 */
public class IndexingTask implements Task {

    /** The dto holding the repository information */
    private RepositoryDto repository;
    /** The IndexingTask to be processed */
    private Set<String> fileNames;
    /* Instantiate a logger  */
    private static final Logger LOG = Logger.getLogger(IndexingTask.class);
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
    /** The XmlConfigurationReader used to get the configuration */
    @Autowired
    private XmlConfigurationReader configReader;
    /** The plugin loader. */
    @Autowired
    private PluginLoader pluginLoader;

    /**
     * executes the task,
     * updates the index fields of the set repository
     * @throws TaskExecutionException
     */
    @Override
    public void execute() throws TaskExecutionException {
        String indexLocation = null;
        try {
            LOG.info("Starting execution of indexing task");
            indexLocation = configReader.getSingleLinePropertyValue("index-location");
            // Read the index status file
            propertiesReader = new PropertiesManager(indexLocation + File.separator + "revisions.properties"); //TODO add propertiesReader path
            // Get the version control plugins
            versionControlPlugin = pluginLoader.getPlugin(VersionControlPlugin.class, repository.getVersionControlSystem());
            versionControlPlugin.setRepository(new URI(repository.getUrl()), repository.getUsername(), repository.getPassword());
            fileNames = versionControlPlugin.getPathsForChangedFilesSinceRevision(propertiesReader.getPropertyFileValue(repository.getName()));
            for (String s : fileNames) {
                LOG.info(s);
            }
            if (fileNames.isEmpty()) {
                LOG.info("Index of repository " + repository.getName() + " is up to date");
                return;
            }
            initializeIndexWriter(new StandardAnalyzer(IndexConstants.LUCENE_VERSION), new File(indexLocation));
            createIndex();
            propertiesReader.setPropertyFileValue(repository.getName(), versionControlPlugin.getRepositoryRevision());
        } catch (VersionControlPluginException ex) {
            LOG.error("VersionControlPlugin files could not be retrieved: " + ex);
        } catch (PluginLoaderException ex) {
            LOG.error("VersionControlPlugin could not be loaded: " + ex);
        } catch (URISyntaxException ex) {
            LOG.error("Repository url could not be parsed to URI" + ex);
        } catch (FileNotFoundException ex) {
            LOG.error("Index not found at task execution" + ex);
        } catch (IOException ex) {
            LOG.error("IOException at execution of task: " + ex);
        } catch (ConfigurationException ex) {
            LOG.error("Configuration could not be read " + ex);
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
            IndexSearcher searcher = new IndexSearcher(indexDirectory);
            indexReader = searcher.getIndexReader();
            LOG.debug("IndexWriter initilaization successful: " + dir.getAbsolutePath());
        } catch (IOException ex) {
            LOG.error(ex);
            LOG.error("IndexWriter initialization error: Could not open directory " + dir.getAbsolutePath());
        }
    }

    /**
     * Creates an index for the given Directory.
     */
    public boolean createIndex() throws VersionControlPluginException, CorruptIndexException, IOException {
        if (indexWriter == null) {
            LOG.error("Creation of indexDirectory failed due to missing initialization of IndexWriter!");
            return false;
        }
        Document doc = new Document();
        try {
            Iterator it = fileNames.iterator();
            int i = 0;
            while (it.hasNext()) {
                String path = (String) it.next();
                if (!(fileIsOnIgnoreList(path))) {
                    // The lucene document containing all relevant indexing information
                    doc = new Document();
                    // Add fields
                    doc = addLuceneFields(doc, path);
                    LOG.debug("Added file: " + doc.get("TITLE") + " to index.");
                    Term term = new Term("TITLE", extractFilename(path));
                    indexReader.deleteDocuments(term);

                    // Add document to the index
                    indexWriter.addDocument(doc);
                    i++;
                }
            }
            indexWriter.commit();
            //indexWriter.optimize(); //TODO check whether optimize makes removing of documents impossible
            indexWriter.close();
        } catch (CorruptIndexException ex) {
            LOG.error("Indexing  of: " + doc.get("title") + " failed! \n" + ex);
        } catch (IOException ex) {
            LOG.error("Adding file to index: " + doc.get("title") + " failed! \n" + ex);
        } catch (NullPointerException ex) {
            LOG.error("NullPointerException: FileContentDirectory is empty!" + ex);
        }
        return true;
    }

    /**
     * Checks whether the current file is on the list of files that will not be indexed
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
    /**
     * Parses the string that represents an entry in the ignore list from the configuration to a string that can be read by java regex
     * @param string the string that is to be parsed
     * @return the regex pattern string
     */
    private String parseRegexString(String string) {
        String retString = string.replace("*", ".*");
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

    public void setRepository(RepositoryDto repository) {
        this.repository = repository;
    }
    
    public void setPluginLoader(PluginLoader pluginLoader) {
        this.pluginLoader = pluginLoader;
    }
}
