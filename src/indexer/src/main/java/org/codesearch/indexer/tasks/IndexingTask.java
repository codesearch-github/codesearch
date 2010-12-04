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
import java.sql.SQLException;
import java.util.logging.Level;
import org.apache.commons.configuration.ConfigurationException;
import org.codesearch.commons.database.DatabaseAccessException;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPluginException;
import org.codesearch.indexer.exceptions.TaskExecutionException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.constants.IndexConstants;
import org.codesearch.commons.database.DBAccess;
import org.codesearch.commons.plugins.PluginLoader;
import org.codesearch.commons.plugins.PluginLoaderException;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPlugin;
import org.codesearch.commons.plugins.codeanalyzing.ast.CompoundNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.Usage;
import org.codesearch.commons.plugins.vcs.FileDto;
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
    /** the FileDtos of the files that have changed since last indexing */
    private Set<FileDto> changedFiles;
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
    private PropertiesManager propertiesManager;
    /** The XmlConfigurationReader used to get the configuration */
    @Autowired
    private XmlConfigurationReader configReader;
    /** The plugin loader. */
    @Autowired
    private PluginLoader pluginLoader;
    /** Defines if the task is set to analyze the class and write code navigation relevant data into the binary index */
    private boolean codeAnalysisEnabled = false;
    private String indexLocation = null;
    private Map<Integer, Usage> usages = new HashMap<Integer, Usage>();

    /**
     * executes the task,
     * updates the index fields of the set repository
     * @throws TaskExecutionException
     */
    @Override
    public void execute() throws TaskExecutionException {
        try {
            LOG.info("Starting execution of indexing task");
            // Read the index status file
            indexLocation = configReader.getSingleLinePropertyValue("index-location");
            propertiesManager = new PropertiesManager(indexLocation + File.separator + "revisions.properties"); //TODO add propertiesReader path
            // Get the version control plugins
            versionControlPlugin = pluginLoader.getPlugin(VersionControlPlugin.class, repository.getVersionControlSystem());
            versionControlPlugin.setRepository(new URI(repository.getUrl()), repository.getUsername(), repository.getPassword());
            String lastIndexedRevision = propertiesManager.getPropertyFileValue(repository.getName());
            changedFiles = versionControlPlugin.getChangedFilesSinceRevision(lastIndexedRevision);
            
            boolean retrieveNewFileList = false;
            executeIndexing();
            if (codeAnalysisEnabled) {
                String lastAnalysisRevision = DBAccess.getLastAnalyzedRevisionOfRepository(repository.getName());
                if (!lastAnalysisRevision.equals(lastIndexedRevision)) {
                    retrieveNewFileList = true;
                } else {
                    retrieveNewFileList = false;
                }
                executeCodeAnalysis(retrieveNewFileList, lastAnalysisRevision);
                DBAccess.setLastAnalyzedRevisionOfRepository(repository.getName(), versionControlPlugin.getRepositoryRevision());
            }
            propertiesManager.setPropertyFileValue(repository.getName(), versionControlPlugin.getRepositoryRevision());
        } catch (DatabaseAccessException ex) {
            LOG.error("Error at DatabaseConnection \n" + ex);
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
        } catch (CodeAnalyzerPluginException ex) {
            LOG.error("Error executing code analysis \n" + ex);
        }
    }

    private void executeCodeAnalysis(boolean retrieveNewFileList, String lastAnalysisRevision) throws VersionControlPluginException, CodeAnalyzerPluginException {
        //TODO test if the retrieving of a new file list works
        if (retrieveNewFileList) {
            changedFiles = versionControlPlugin.getChangedFilesSinceRevision(lastAnalysisRevision);
        }
        CodeAnalyzerPlugin plugin = null;
        String currentFileType = null;
        String previousFileType = null;
        for (FileDto currentFile : changedFiles) {
            try {
                //load the appropriate plugin
                try {
                    currentFileType = currentFile.getFilePath().substring(currentFile.getFilePath().lastIndexOf(".") + 1); //TODO add handling for files without file endings or hidden files
                } catch (StringIndexOutOfBoundsException ex) {
                    continue;
                }
                if (plugin == null || (!currentFileType.equals(previousFileType))) {
                    //load the appropriate plugin
                    try {
                        plugin = pluginLoader.getPlugin(CodeAnalyzerPlugin.class, currentFileType);
                    } catch (PluginLoaderException ex) {
                        //in case there is no codeanalyzer plugin for the file ending
                        continue;
                    }
                }
                plugin.analyzeFile(currentFile.getContent().toString(), repository);
                Map<Integer, CompoundNode> ast = plugin.getAstForCurrentFile();
//                for (Entry<Integer, CompoundNode> currentEntry : ast.entrySet()) {
//                    System.out.println(currentEntry.getValue().getOutlineLink());
//                }
                previousFileType = currentFileType;
                DBAccess.setBinaryIndexForFile(currentFile.getFilePath(), repository.getName(), ast);
            } catch (DatabaseAccessException ex) {
                LOG.error("Error at DatabaseConnection \n" + ex);
            }
        }
    }

    private void executeIndexing() throws IOException, ConfigurationException, VersionControlPluginException {
        if (changedFiles.isEmpty()) {
            LOG.info("Index of repository " + repository.getName() + " is up to date");
            return;
        }
        File dir = new File(indexLocation);
        indexDirectory = FSDirectory.open(dir);

        clearPreviousDocuments();
        initializeIndexWriter(new WhitespaceAnalyzer(), dir); //IndexConstants.LUCENE_VERSION
        createIndex();
    }

    /**
     * Adds the Fields to the lucene document.
     * @param doc the target document
     * @return document with added lucene fields
     */
    public Document addLuceneFields(Document doc, FileDto file) throws VersionControlPluginException {
        doc.add(new Field(IndexConstants.INDEX_FIELD_TITLE, extractFilename(file.getFilePath()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field(IndexConstants.INDEX_FIELD_FILEPATH, file.getFilePath(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        if (!file.isBinary()) {
            doc.add(new Field(IndexConstants.INDEX_FIELD_CONTENT, new String(file.getContent()), Field.Store.YES, Field.Index.ANALYZED));
            doc.add(new Field(IndexConstants.INDEX_FIELD_CONTENT_LC, new String(file.getContent()).toLowerCase(), Field.Store.YES, Field.Index.ANALYZED));
        }
        doc.add(new Field(IndexConstants.INDEX_FIELD_REPOSITORY, repository.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field(IndexConstants.INDEX_FIELD_REVISION, versionControlPlugin.getRepositoryRevision(), Field.Store.YES, Field.Index.ANALYZED));
        //doc.add(new Field(IndexConstants.INDEX_FILED_REPOSITORY_GROUP, repository.getRepositoryGroupsAsString(), Field.Store.YES, Field.Index.ANALYZED));
        //doc.add(new Field(IndexConstants.INDEX_FIELD_TITLE_LC, extractFilename(path).toLowerCase(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field(IndexConstants.INDEX_FIELD_FILEPATH_LC, file.getFilePath().toLowerCase(), Field.Store.YES, Field.Index.NOT_ANALYZED));
      // doc.add(new Field(IndexConstants.INDEX_FIELD_FILE_TYPE, file.getMimeType(), Field.Store.YES, Field.Index.ANALYZED)); //TODO add mime type
        return doc;
    }

    /**
     * Initializes a IndexWriter with the given analyzer and for the given directory
     * @param analyzer analyzer for the files.
     * @param dir The location for the  lucene index
     */
    public void initializeIndexWriter(Analyzer luceneAnalyzer, File dir) {
        try {
            indexWriter = new IndexWriter(indexDirectory, luceneAnalyzer, IndexWriter.MaxFieldLength.LIMITED);
            LOG.debug("IndexWriter initilaization successful: " + dir.getAbsolutePath());
        } catch (IOException ex) {
            LOG.error(ex);
            LOG.error("IndexWriter initialization error: Could not open directory " + dir.getAbsolutePath());
        }
    }

    /**
     * Creates an index for the given Directory.
     */
    private boolean createIndex() throws VersionControlPluginException, CorruptIndexException, IOException {
        if (indexWriter == null) {
            LOG.error("Creation of indexDirectory failed due to missing initialization of IndexWriter!");
            return false;
        }
        Document doc = new Document();
        try {
            Iterator it = changedFiles.iterator();
            int i = 0;
            while (it.hasNext()) {
                FileDto file = (FileDto) it.next();
                if (!(fileIsOnIgnoreList(file.getFilePath()))) {
                    // The lucene document containing all relevant indexing information
                    doc = new Document();
                    // Add fields
                    doc = addLuceneFields(doc, file);
                    LOG.debug("Added file: " + doc.get(IndexConstants.INDEX_FIELD_TITLE) + " to index.");
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
     * removes all documents from the index that will be replaced by updated documents
     */
    private void clearPreviousDocuments() throws CorruptIndexException, IOException {
        IndexSearcher searcher = null;
        try {
            searcher = new IndexSearcher(indexDirectory, false);
        } catch (IOException ex) {
            //In case no index was found no documents have to be deleted
            return;
        }
        indexReader = searcher.getIndexReader();
        for (FileDto file : changedFiles) {
            Term term = new Term(IndexConstants.INDEX_FIELD_FILEPATH, file.getFilePath());
            indexReader.deleteDocuments(term);
        }
        indexReader.close();
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

    public void setRepository(RepositoryDto repository) {
        this.repository = repository;
    }

    public void setPluginLoader(PluginLoader pluginLoader) {
        this.pluginLoader = pluginLoader;
    }

    public boolean isCodeAnalysisEnabled() {
        return codeAnalysisEnabled;
    }

    public void setCodeAnalysisEnabled(boolean codeAnalysisEnabled) {
        this.codeAnalysisEnabled = codeAnalysisEnabled;
    }
}
