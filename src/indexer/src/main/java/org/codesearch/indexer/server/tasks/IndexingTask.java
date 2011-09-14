/**
 * Copyright 2010 David Froehlich <david.froehlich@businesssoftware.at>, Samuel Kogler <samuel.kogler@gmail.com>, Stephan Stiboller
 * <stistc06@htlkaindorf.at>
 * 
 * This file is part of Codesearch.
 * 
 * Codesearch is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * Codesearch is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Codesearch. If not, see <http://www.gnu.org/licenses/>.
 */
package org.codesearch.indexer.server.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.FSDirectory;
import org.codesearch.commons.configuration.properties.PropertiesManager;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.constants.IndexConstants;
import org.codesearch.commons.database.DBAccess;
import org.codesearch.commons.database.DatabaseAccessException;
import org.codesearch.commons.plugins.PluginLoader;
import org.codesearch.commons.plugins.PluginLoaderException;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPlugin;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPluginException;
import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.Usage;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldPlugin;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldValueException;
import org.codesearch.commons.plugins.vcs.FileDto;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;
import org.codesearch.commons.plugins.vcs.VersionControlPluginException;
import org.codesearch.commons.utils.mime.MimeTypeUtil;
import org.codesearch.indexer.server.exceptions.InvalidIndexLocationException;
import org.codesearch.indexer.server.exceptions.NotifySearcherException;
import org.codesearch.indexer.server.exceptions.TaskExecutionException;
import org.codesearch.indexer.server.manager.IndexingJob;

import com.google.inject.Inject;

/**
 * This task performs basic indexing of one repository.
 * 
 * @author Stephan Stiboller
 * @author David Froehlich
 */
public class IndexingTask implements Task {

    /** The Logger. */
    private static final Logger LOG = Logger.getLogger(IndexingTask.class);
    /** The affected repositories. */
    private List<RepositoryDto> repositories;
    /** The currently active IndexWriter */
    private IndexWriter indexWriter;
    /** The index directory, contains all index files */
    private FSDirectory indexDirectory;
    /** The Version control Plugin */
    private VersionControlPlugin versionControlPlugin;
    /** The used PropertyReader */
    private PropertiesManager propertiesManager;
    /** the location of the lucene index */
    private String indexLocation = null;
    /** the plugins that will be used to create the fields for each document */
    private List<LuceneFieldPlugin> luceneFieldPlugins = new LinkedList<LuceneFieldPlugin>();
    /** The database access object */
    private DBAccess dba;
    /** The plugin loader. */
    private PluginLoader pluginLoader;
    /** The URI that is called to update the searcher application. */
    private String searcherUpdatePath;
    /** The parent {@link IndexingJob}. */
    private IndexingJob job;

    @Inject
    public IndexingTask(DBAccess dba, PluginLoader pluginLoader, String searcherUpdatePath) {
        this.searcherUpdatePath = searcherUpdatePath;
        this.dba = dba;
        this.pluginLoader = pluginLoader;
    }

    @Override
    public void setRepositories(List<RepositoryDto> repositories) {
        this.repositories = repositories;
    }

    @Override
    public void setIndexLocation(String indexLocation) {
        this.indexLocation = indexLocation;
    }

    /**
     * executes the task, updates the index fields of the set repository
     * 
     * @throws TaskExecutionException
     */
    @Override
    public void execute() throws TaskExecutionException {
        if (repositories != null) {
            StringBuilder repos = new StringBuilder();
            for (RepositoryDto repositoryDto : repositories) {
                repos.append(repositoryDto.getName()).append(" ");
            }
            LOG.info("Starting indexing of repositories: " + repos.toString().trim());
            try {
                init();
                int i = 0;
                for (RepositoryDto repository : repositories) {
                    job.setCurrentRepository(i);
                    try {
                        LOG.info("Indexing repository: " + repository.getName());
                        long start = System.currentTimeMillis();
                        // Read the index status file
                        String lastIndexedRevision = propertiesManager.getPropertyFileValue(repository.getName());
                        LOG.info("Last indexed revision is: " + lastIndexedRevision);
                        // Get the version control plugins
                        versionControlPlugin = pluginLoader.getPlugin(VersionControlPlugin.class, repository.getVersionControlSystem());
                        versionControlPlugin.setRepository(repository);
                        LOG.info("Newest revision is      : " + versionControlPlugin.getRepositoryRevision());
                        Set<FileDto> changedFiles = versionControlPlugin.getChangedFilesSinceRevision(lastIndexedRevision);
                        LOG.info(changedFiles.size() + " files have changed since the last indexing");

                        indexFiles(changedFiles);
                        propertiesManager.setPropertyFileValue(repository.getName(), versionControlPlugin.getRepositoryRevision());
                        long duration = System.currentTimeMillis() - start;
                        LOG.info("Lucene indexing took " + duration / 1000 + " seconds");

                        if (repository.isCodeNavigationEnabled()) {
                            try {
                                LOG.info("Starting code analyzing");
                                start = System.currentTimeMillis();
                                boolean retrieveNewFileList = false;

                                String lastAnalysisRevision = dba.getLastAnalyzedRevisionOfRepository(repository.getName());
                                if (!lastAnalysisRevision.equals(lastIndexedRevision)) {
                                    retrieveNewFileList = true;
                                } else {
                                    retrieveNewFileList = false;
                                }
                                executeCodeAnalysis(repository, changedFiles, retrieveNewFileList, lastAnalysisRevision);
                                dba.setLastAnalyzedRevisionOfRepository(repository.getName(), versionControlPlugin.getRepositoryRevision());
                                duration = System.currentTimeMillis() - start;
                                LOG.info("Code analyzing took " + duration / 1000 + " seconds");
                            } catch (DatabaseAccessException ex) {
                                LOG.error("Code analyzing failed: Database error:" + ex);
                            }
                        }
                    } catch (PluginLoaderException ex) {
                        LOG.error("VersionControlPlugin could not be loaded: " + ex);
                    } catch (VersionControlPluginException ex) {
                        LOG.error("Files could not be retrieved: " + ex);
                    }
                    i++;
                }
                try {
                    // notify the searcher about the update of the indexer
                    notifySearcher();
                } catch (NotifySearcherException ex) {
                    LOG.warn("Notification of searcher failed, changes in the index will not be recognized without a restart: " + ex);
                }
                LOG.info("Finished indexing");
            } catch (InvalidIndexLocationException ex) {
                LOG.error("Invalid index location: " + ex);
            } catch (FileNotFoundException ex) {
                LOG.error("Could not write the index status file: " + ex);
            } catch (IOException ex) {
                LOG.error("IOException occured at indexing: " + ex);
            } finally {
                cleanup();
            }
        } else {
            LOG.warn("No repositories specified, skipping indexing.");
        }
    }

    /**
     * Sends a request to the searcher web application, notifying it to re-load the index.
     * 
     * @throws NotifySearcherException in case the connection to the searcher could not be established
     */
    private void notifySearcher() throws NotifySearcherException {
        try {
            URL url = new URL(searcherUpdatePath);
            url.openStream();
        } catch (IOException ex) {
            throw new NotifySearcherException("Could not connect to searcher at the configured address: " + searcherUpdatePath + "\n"
                    + ex.toString());
        }
    }

    /**
     * executes the code analysis for the currently set repository
     * 
     * @param retrieveNewFileList determines whether the code analysis should retrieve a new list of changed files, needed if the
     *            lastIndexedRevision (stored in a properties file in the index directory) differs from the lastAnalysisRevision (stored in
     *            the database)
     * @param lastAnalysisRevision the revision of the repository where code analysis was last executed
     * @throws VersionControlPluginException if no VCP could be loaded for the set repository
     * @throws CodeAnalyzerPluginException if the source code of one of the files could not be analyzed
     */
    private void executeCodeAnalysis(RepositoryDto repository, Set<FileDto> changedFiles, boolean retrieveNewFileList,
            String lastAnalysisRevision) throws VersionControlPluginException, DatabaseAccessException {
        if (retrieveNewFileList) {
            changedFiles = versionControlPlugin.getChangedFilesSinceRevision(lastAnalysisRevision);
        }

        // For better efficiency, first sort all files by file type and only
        // load plugin once per file type
        Map<String, Set<FileDto>> filetypeMap = new HashMap<String, Set<FileDto>>();
        for (FileDto currentFile : changedFiles) {
            if (currentFile.isDeleted()) {
                dba.deleteFile(currentFile.getFilePath(), repository.getName());
                LOG.debug("Deleted all records associated with " + currentFile.getFilePath() + " since it was deleted from the file system");
            } else {
                String fileType = MimeTypeUtil.guessMimeTypeViaFileEnding(currentFile.getFilePath());
                if (!fileType.equals(MimeTypeUtil.UNKNOWN)) {
                    Set<FileDto> files = filetypeMap.get(fileType);
                    if (files == null) {
                        files = new HashSet<FileDto>();
                        filetypeMap.put(fileType, files);
                    }
                    files.add(currentFile);
                }
            }
        }
        CodeAnalyzerPlugin plugin = null;
        try {
            for (String filetype : filetypeMap.keySet()) {
                LOG.info("Analyzing files with type: " + filetype);
                try {
                    plugin = pluginLoader.getPlugin(CodeAnalyzerPlugin.class, filetype);
                    for (FileDto currentFile : filetypeMap.get(filetype)) {
                        LOG.debug("Analyzing file: " + currentFile.getFilePath());
                        try {
                            plugin.analyzeFile(new String(currentFile.getContent()));
                            AstNode ast = plugin.getAst();
                            List<String> typeDeclarations = plugin.getTypeDeclarations();
                            List<Usage> usages = plugin.getUsages();
                            List<String> imports = plugin.getImports();
                            // add the externalLinks to the FileDto, so they
                            // can be parsed after the regular indexing is finished
                            // write the AST information into the database
                            dba.setAnalysisDataForFile(currentFile.getFilePath(), repository.getName(), ast, usages, typeDeclarations,
                                    imports);
                        } catch (CodeAnalyzerPluginException ex) {
                            LOG.error("Could not analyze file: " + currentFile.getFilePath() + "\n" + ex);
                        }
                    }
                } catch (PluginLoaderException ex) {
                    LOG.info("No codeanalyzing plugin found for file type: " + filetype);
                }
            }
        } catch (DatabaseAccessException ex) {
            LOG.error("Code analyzing could not be executed because the database connection failed: \n" + ex);
        }
    }

    /**
     * Indexes the specified changed files of the specified repository.
     * 
     * @throws IOException if some error occured while indexing
     * @throws VersionControlPluginException if the current revision could not be determined from the VersionControlPlugin
     */
    private void indexFiles(Set<FileDto> changedFiles) throws IOException, VersionControlPluginException {
        deleteFilesFromIndex(changedFiles);
        addFilesToIndex(changedFiles);
        indexWriter.commit();
    }

    /**
     * Adds all fields of the specified file to the specified document.
     * 
     * @param doc The target document
     * @param file The source file
     */
    private void addLuceneFields(Document doc, FileDto file) throws LuceneFieldValueException {
        for (LuceneFieldPlugin currentPlugin : luceneFieldPlugins) {
            String fieldValue = currentPlugin.getFieldValue(file);
            String currentFieldName = currentPlugin.getFieldName();
            Store store = currentPlugin.isStored() ? Field.Store.YES : Field.Store.NO;
            Index index = currentPlugin.isAnalyzed() ? Field.Index.ANALYZED : Field.Index.NOT_ANALYZED;
            doc.add(new Field(currentFieldName, fieldValue, store, index));
            if (currentPlugin.addLowercase()) {
                doc.add(new Field(currentFieldName + "_lc", fieldValue.toLowerCase(), store, index));
                // TODO move _lc to a constant
            }
        }
    }

    /**
     * Initializes Lucene IndexWriter, loads plugins etc..
     */
    private void init() throws InvalidIndexLocationException {
        propertiesManager = new PropertiesManager(indexLocation + IndexConstants.REVISIONS_PROPERTY_FILENAME);
        File dir = new File(indexLocation);
        try {
            indexDirectory = FSDirectory.open(dir);
        } catch (IOException ex) {
            throw new InvalidIndexLocationException("Cannot access index directory at: " + indexLocation);
        }

        PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new SimpleAnalyzer(IndexConstants.LUCENE_VERSION));
        try {
            for (LuceneFieldPlugin currentPlugin : pluginLoader
                    .getMultiplePluginsForPurpose(LuceneFieldPlugin.class, "lucene_field_plugin")) {
                luceneFieldPlugins.add(currentPlugin);
                analyzer.addAnalyzer(currentPlugin.getFieldName(), currentPlugin.getRegularCaseAnalyzer());
                if (currentPlugin.addLowercase()) {
                    analyzer.addAnalyzer(currentPlugin.getFieldName() + "_lc", currentPlugin.getLowerCaseAnalyzer());
                }
            }
        } catch (PluginLoaderException ex) {
            LOG.warn("No LuceneFieldPlugins could be found.");
        }
        try {
            IndexWriterConfig config = new IndexWriterConfig(IndexConstants.LUCENE_VERSION, analyzer);
            indexWriter = new IndexWriter(indexDirectory, config);
            LOG.debug("IndexWriter initialization successful: " + dir.getAbsolutePath());
        } catch (IOException ex) {
            LOG.error("IndexWriter initialization error: " + ex);
        }
    }

    /**
     * Performs cleanup tasks after execution or after an error is encountered.
     */
    private void cleanup() {
        if (indexWriter != null) {
            try {
                indexWriter.close();
            } catch (IOException ex) {
                LOG.error("Could not close the index writer:\n" + ex);
            } catch (OutOfMemoryError error) {
                LOG.error("Out of memory, trying to save index");
                Runtime.getRuntime().gc();
                try {
                    indexWriter.close();
                } catch (IOException ex) {
                    LOG.error("Could not close the index writer:\n" + ex);
                }
            }
        }
        if (indexDirectory != null) {
            indexDirectory.close();
        }
    }

    /**
     * Adds the specified files to the index.
     */
    private boolean addFilesToIndex(Set<FileDto> files) throws VersionControlPluginException, CorruptIndexException, IOException {
        // FIXME: improve error handling
        if (indexWriter == null) {
            LOG.error("Creation of indexDirectory failed due to missing initialization of IndexWriter!");
            return false;
        }
        Document doc = new Document();
        try {
            for (FileDto file : files) {
                if (file == null || file.getFilePath() == null) {
                    continue; // TODO find out why this can happen
                }

                if ((!file.isDeleted()) && (shouldFileBeIndexed(file))) {
                    // The lucene document containing all relevant indexing information
                    doc = new Document();
                    // Add fields
                    addLuceneFields(doc, file);
                    // Add document to the index
                    indexWriter.addDocument(doc);

                    // Logging
                    if (LOG.isDebugEnabled()) {
                        String fileName;
                        try {
                            fileName = file.getFilePath().substring(file.getFilePath().lastIndexOf("/") + 1);
                        } catch (StringIndexOutOfBoundsException ex) {
                            // if the file is in the root directory of the repository
                            fileName = file.getFilePath();
                        }
                        LOG.debug("Added file: " + fileName + " to index.");
                    }
                }
            }
        } catch (LuceneFieldValueException ex) {
            LOG.error("Determination of the field content failed\n" + ex);
        } catch (CorruptIndexException ex) {
            LOG.error("Indexing  of: " + doc.get("title") + " failed! \n" + ex);
        } catch (IOException ex) {
            LOG.error("Adding file to index: " + doc.get("title") + " failed! \n" + ex);
        }
        return true;
    }

    /**
     * Removes the specified files from the index.
     */
    private void deleteFilesFromIndex(Set<FileDto> files) {
        try {
            for (FileDto file : files) {
                // FIXME this is now done via plugins -> what if constant changes?!
                Term repositoryTerm = new Term(IndexConstants.INDEX_FIELD_REPOSITORY, file.getRepository().getName());
                Term filepathTerm = new Term(IndexConstants.INDEX_FIELD_FILEPATH, file.getFilePath());
                BooleanQuery query = new BooleanQuery();
                query.add(new BooleanClause(new TermQuery(repositoryTerm), Occur.MUST));
                query.add(new BooleanClause(new TermQuery(filepathTerm), Occur.MUST));

                indexWriter.deleteDocuments(query);
            }
        } catch (CorruptIndexException ex) {
            LOG.error("Could not delete files from index because it is corrupted.");
        } catch (IOException ex) {
            LOG.error("Could not delete files from index: " + ex);
        }
    }

    /**
     * Checks whether the current file is on the list of files that will not be indexed
     */
    private boolean shouldFileBeIndexed(FileDto file) {
        String path = file.getFilePath();
        RepositoryDto repository = file.getRepository();
        Pattern p;
        boolean matchesElementOnWhitelist = false;
        boolean shouldFileBeIndexed = true;
        // if no whitelist is specified all files pass the whitelist check
        if (repository.getWhitelistEntries().isEmpty()) {
            matchesElementOnWhitelist = true;
        } else {
            // else check if the filename matches one of the whitelist entries
            for (String currentWhitelistEntry : repository.getWhitelistEntries()) {
                p = Pattern.compile(currentWhitelistEntry);
                Matcher m = p.matcher(path);
                if (m.find()) {
                    matchesElementOnWhitelist = true;
                    break;
                }
            }
        }
        // check if the filename matches one of the blacklist entries, if yes
        // return false, so the file won't be indexed
        if (matchesElementOnWhitelist) {
            for (String currentBlacklistEntry : repository.getBlacklistEntries()) {
                p = Pattern.compile(currentBlacklistEntry);
                Matcher m = p.matcher(path);
                if (m.find()) {
                    shouldFileBeIndexed = false;
                    break;
                }
            }
        }
        return shouldFileBeIndexed && matchesElementOnWhitelist;
    }

    /** {@inheritDoc} */
    @Override
    public void setJob(IndexingJob job) {
        this.job = job;
    }
}