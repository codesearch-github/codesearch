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

import java.io.*;
import java.net.URISyntaxException;
import org.apache.commons.configuration.ConfigurationException;
import org.codesearch.commons.database.DatabaseAccessException;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPluginException;
import org.codesearch.indexer.exceptions.TaskExecutionException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
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
import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
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
    /** the location of the lucene index */
    private String indexLocation = null;
    /* Logger */
    private static final Logger LOG = Logger.getLogger(IndexingTask.class);

    /**
     * executes the task,
     * updates the index fields of the set repository
     * @throws TaskExecutionException
     */
    @Override
    public void execute() throws TaskExecutionException {
        try {
            LOG.info("Starting indexing of repository: " + repository.getName());
            // Read the index status file
            indexLocation = configReader.getSingleLinePropertyValue("index-location");
            propertiesManager = new PropertiesManager(indexLocation + "revisions.properties");
            String lastIndexedRevision = propertiesManager.getPropertyFileValue(repository.getName());
            LOG.info("Last indexed revision is: " + lastIndexedRevision);
            // Get the version control plugins
            versionControlPlugin = pluginLoader.getPlugin(VersionControlPlugin.class, repository.getVersionControlSystem());
            versionControlPlugin.setRepository(new URI(repository.getUrl()), repository.getUsername(), repository.getPassword());
            LOG.info("Newest revision is      : " + versionControlPlugin.getRepositoryRevision());
            changedFiles = versionControlPlugin.getChangedFilesSinceRevision(lastIndexedRevision);

            LOG.info("Changed files: ");
            for(FileDto f : changedFiles) {
                LOG.debug(f.getFilePath());
            }

            boolean retrieveNewFileList = false;
            
            executeIndexing();
            propertiesManager.setPropertyFileValue(repository.getName(), versionControlPlugin.getRepositoryRevision());
            
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
        } catch (org.apache.commons.configuration.ConfigurationException ex) {
            LOG.error("Error at DatabaseConnection \n" + ex);
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
        } catch (CodeAnalyzerPluginException ex) {
            LOG.error("Error executing code analysis \n" + ex);
        }
         LOG.info("Finished indexing of repository: " + repository.getName());
    }

    /**
     * executes the code analysis for the currently set repository
     * @param retrieveNewFileList determines whether the code analysis should retrieve a new list of changed files,
     * needed if the lastIndexedRevision (stored in a properties file in the index directory) differs from the lastAnalysisRevision (stored in the database)
     * @param lastAnalysisRevision the revision of the repository where code analysis was last executed
     * @throws VersionControlPluginException if no VCP could be loaded for the set repository
     * @throws CodeAnalyzerPluginException if the source code of one of the files could not be analyzed
     */
    private void executeCodeAnalysis(boolean retrieveNewFileList, String lastAnalysisRevision) throws VersionControlPluginException, CodeAnalyzerPluginException {
        LOG.info("Starting code analysis");
        if (retrieveNewFileList) {
            changedFiles = versionControlPlugin.getChangedFilesSinceRevision(lastAnalysisRevision);
        }
        //the code analyzer plugin used to analyze the entire repository
        CodeAnalyzerPlugin plugin = null;
        //in case a file has the same filetype (and needs the same analyzer) as the file before the plugin will not be loaded a second time
        String currentFileType = null;
        String previousFileType = null;
        for (FileDto currentFile : changedFiles) {
            try {
                try {
                    currentFileType = currentFile.getFilePath().substring(currentFile.getFilePath().lastIndexOf(".") + 1); //TODO add handling for files without file endings or hidden files
                } catch (StringIndexOutOfBoundsException ex) {
                    //In case the file has no ending no code analyzis will be executed
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

                plugin.analyzeFile(new String(currentFile.getContent()));
                AstNode ast = plugin.getAst();
                List<String> typeDeclarations = plugin.getTypeDeclarations();
                List<Usage> usages = plugin.getUsages();
                List<String> imports = plugin.getImports();
                //List<ExternalUsage> externalUsages =
                previousFileType = currentFileType;
                //add the externalLinks to the FileDto, so they can be parsed after the regular indexing is finished
                //write the AST information into the database
                DBAccess.setAnalysisDataForFile(currentFile.getFilePath(), repository.getName(), ast, usages, typeDeclarations, imports);
                LOG.debug("Analyzed file: " + currentFile.getFilePath());
            } catch (DatabaseAccessException ex) {
                LOG.error("Error at DatabaseConnection \n" + ex);
            }
        }
        LOG.info("Finished code analysis");
    }

    /**
     * creates/updates the index of all files in the changedFiles list
     * @throws IOException if the index could not be read
     * @throws ConfigurationException if the necessary configuration could not be read from the config file
     * @throws VersionControlPluginException if the current revision could not be determined from the VersionControlPlugin
     */
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
            LOG.debug("IndexWriter initialization successful: " + dir.getAbsolutePath());
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
            int i = 0;
            for (FileDto file : changedFiles) {
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
            LOG.error("NullPointerException: FileContentDirectory is empty! " + ex);
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

    public boolean isCodeAnalysisEnabled() {
        return codeAnalysisEnabled;
    }

    public void setCodeAnalysisEnabled(boolean codeAnalysisEnabled) {
        this.codeAnalysisEnabled = codeAnalysisEnabled;
    }
}
