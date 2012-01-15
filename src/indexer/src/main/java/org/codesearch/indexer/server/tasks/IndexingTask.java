/**
 * Copyright 2010 David Froehlich <david.froehlich@businesssoftware.at>, Samuel
 * Kogler <samuel.kogler@gmail.com>, Stephan Stiboller <stistc06@htlkaindorf.at>
 *
 * This file is part of Codesearch.
 *
 * Codesearch is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Codesearch is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Codesearch. If not, see <http://www.gnu.org/licenses/>.
 */
package org.codesearch.indexer.server.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
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
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.FSDirectory;
import org.codesearch.commons.configuration.dto.RepositoryDto;
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
import org.codesearch.commons.plugins.vcs.FileIdentifier;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;
import org.codesearch.commons.plugins.vcs.VersionControlPluginException;
import org.codesearch.commons.utils.mime.MimeTypeUtil;
import org.codesearch.indexer.server.exceptions.InvalidIndexLocationException;
import org.codesearch.indexer.server.exceptions.NotifySearcherException;
import org.codesearch.indexer.server.exceptions.TaskExecutionException;
import org.codesearch.indexer.server.manager.IndexingJob;

import com.google.inject.Inject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.codesearch.commons.configuration.properties.PropertiesManager;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldPluginLoader;
import org.codesearch.commons.plugins.vcs.FileDto;

/**
 * This task performs basic indexing of one repository.
 *
 * @author Stephan Stiboller
 * @author David Froehlich
 */
public class IndexingTask implements Task {

    /**
     * The Logger.
     */
    private static final Logger LOG = Logger.getLogger(IndexingTask.class);
    /**
     * The affected repositories.
     */
    private List<RepositoryDto> repositories;
    /**
     * The currently active IndexWriter
     */
    private IndexWriter indexWriter;
    /**
     * The index directory, contains all index files
     */
    private FSDirectory indexDirectory;
    /**
     * The Version control Plugin
     */
    private VersionControlPlugin versionControlPlugin;
    /**
     * used to read the repository revision status
     */
    private PropertiesManager propertiesManager;
    /**
     * the location of the lucene index
     */
    private File indexLocation = null;
    /**
     * the plugins that will be used to create the fields for each document
     */
    private List<LuceneFieldPlugin> luceneFieldPlugins = new LinkedList<LuceneFieldPlugin>();
    /**
     * The database access object
     */
    private DBAccess dba;
    /**
     * The plugin loader.
     */
    private PluginLoader pluginLoader;
    /**
     * The URI that is called to update the searcher application.
     */
    private URI searcherUpdatePath;
    /**
     * The parent {@link IndexingJob}.
     */
    private IndexingJob job;
    /**
     * the CodeAnalyzerPlugins used, one per mimetype
     */
    private Map<String, CodeAnalyzerPlugin> caPlugins = new HashMap<String, CodeAnalyzerPlugin>();
    /**
     * The wrapper analyzer constructed by the plugin loader
     */
    private PerFieldAnalyzerWrapper caseInsensitiveAnalyzer;

    @Inject
    public IndexingTask(DBAccess dba, PluginLoader pluginLoader, URI searcherUpdatePath, LuceneFieldPluginLoader luceneFieldPluginLoader, PropertiesManager propertiesManager) {
        luceneFieldPlugins = luceneFieldPluginLoader.getAllLuceneFieldPlugins();
        caseInsensitiveAnalyzer = luceneFieldPluginLoader.getPerFieldAnalyzerWrapper(false);
        this.propertiesManager = propertiesManager;

        this.searcherUpdatePath = searcherUpdatePath;
        this.dba = dba;
        this.pluginLoader = pluginLoader;
    }

    @Override
    public void setRepositories(List<RepositoryDto> repositories) {
        this.repositories = repositories;
    }

    @Override
    public void setIndexLocation(File indexLocation) {
        this.indexLocation = indexLocation;
    }

    /**
     * executes the task, updates the index fields of the set repository
     *
     * @throws TaskExecutionException
     */
    @Override
    public void execute() throws TaskExecutionException {
        //whether or not previous database operations were executed successfully
        //once a DB-operation fails no additional operations are executed in this task to prevent log flooding
        boolean databaseConnectionValid = true;
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
                    if (job != null) {
                        job.setCurrentRepository(i);
                    }
                    try {
                        LOG.info("Indexing repository: " + repository.getName() + (repository.isCodeNavigationEnabled() ? " codenavigation enabled" : " codenavigation disabled"));
                        long start = System.currentTimeMillis();
                        // Read the index status file
                        String lastIndexedRevision = propertiesManager.getValue(repository.getName());
                        LOG.info("Last indexed revision is: " + lastIndexedRevision);
                        // Get the version control plugins
                        versionControlPlugin = pluginLoader.getPlugin(VersionControlPlugin.class, repository.getVersionControlSystem());
                        // get the changed files
                        versionControlPlugin.setRepository(repository);
                        LOG.info("Newest revision is      : " + versionControlPlugin.getRepositoryRevision());
                        Set<FileIdentifier> changedFiles = versionControlPlugin.getChangedFilesSinceRevision(lastIndexedRevision);
                        LOG.info(changedFiles.size() + " files have changed since the last indexing");
                        // clear the index of the old verions of the files
                        deleteFilesFromIndex(changedFiles);
                        if (repository.isCodeNavigationEnabled()) {
                            try {
                                String lastAnalysisRevision = dba.getLastAnalyzedRevisionOfRepository(repository.getName());
                                if (!lastAnalysisRevision.equals(lastIndexedRevision)) {
                                    throw new TaskExecutionException("The code informaiton in the database is not at the same revision as the regular indexed information\n"
                                            + "The index of the repository must be cleared via a ClearTask");
                                }
                                deleteFilesFromDatabase(changedFiles);
                            } catch (DatabaseAccessException ex) {
                                LOG.error("Code analyzing failed, will attempt to execute regular indexing but no code analysis will take place: Database error:" + ex);
                                databaseConnectionValid = false;
                            }
                        }
                        // check whether the changed files should be indexed
                        removeNotToBeIndexedFiles(changedFiles);
                        for (FileIdentifier currentIdentifier : changedFiles) {
                            if (!currentIdentifier.isDeleted()) {
                                try {
                                    FileDto currentDto = versionControlPlugin.getFileDtoForFileIdentifierAtRevision(currentIdentifier, VersionControlPlugin.CURRENT_VERSION);
                                    addFileToIndex(currentDto);

                                    if (repository.isCodeNavigationEnabled() && databaseConnectionValid) {
                                        executeCodeAnalysisForFile(currentDto);
                                    }

                                } catch (CodeAnalyzerPluginException ex) {
                                    LOG.error("Code analyzing failed, skipping file\n" + ex);
                                    //in case either of those exceptions occurs try to keep indexing the remaining files
                                } catch (LuceneFieldValueException ex) {
                                    LOG.error(ex);
                                } catch (DatabaseAccessException ex) {
                                    LOG.error("Code analyzing failed: Database error:" + ex);
                                    databaseConnectionValid = false;
                                }
                            }
                        }
                        long duration = System.currentTimeMillis() - start;
                        LOG.info("Indexing of repository " + repository.getName() + " took " + duration / 1000 + " seconds");
                        propertiesManager.setValue(repository.getName(), versionControlPlugin.getRepositoryRevision());
                        if (repository.isCodeNavigationEnabled()) {
                            try {
                                dba.setLastAnalyzedRevisionOfRepository(repository.getName(), versionControlPlugin.getRepositoryRevision());
                            } catch (DatabaseAccessException ex) {
                                databaseConnectionValid = false;
                            }
                        }
                    } catch (PluginLoaderException ex) {
                        LOG.error("Could not load VersionControlPlugin\n" + ex);
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

    private void deleteFilesFromDatabase(Set<FileIdentifier> identifier) throws DatabaseAccessException {
        for (FileIdentifier currentIdentifier : identifier) {
            dba.deleteFile(currentIdentifier.getFilePath(), currentIdentifier.getRepository().getName());
        }
    }

    /**
     * retrieves the set of all FileDtos for the given FileIdentifiers
     *
     * @param fileIdentifiers
     * @param plugin
     * @return
     * @throws VersionControlPluginException
     */
    @Deprecated
    private Set<FileDto> retrieveFileDtosForIdentifiers(Set<FileIdentifier> fileIdentifiers, VersionControlPlugin plugin) throws VersionControlPluginException {
        //TODO find out if this method is still needed
        Set<FileDto> fileDtos = new HashSet<FileDto>();
        for (FileIdentifier currentIdentifier : fileIdentifiers) {
            fileDtos.add(plugin.getFileDtoForFileIdentifierAtRevision(currentIdentifier, VersionControlPlugin.CURRENT_VERSION));
        }
        return fileDtos;
    }

    /**
     * checks all FileIdentifiers whether the files should be indexed or not
     * deletes those files that should not be indexed from the set
     *
     * @param fileIdentifiers
     */
    private void removeNotToBeIndexedFiles(Set<FileIdentifier> fileIdentifiers) {
        if (!fileIdentifiers.isEmpty()) {
            Iterator<FileIdentifier> iter = fileIdentifiers.iterator();
            FileIdentifier currentFile = iter.next();
            for (; iter.hasNext(); currentFile = iter.next()) {
                if ((currentFile.isDeleted()) || (!shouldFileBeIndexed(currentFile))) {
                    iter.remove();
                }
            }
        }
    }

    /**
     * Sends a request to the searcher web application, notifying it to re-load
     * the index.
     *
     * @throws NotifySearcherException in case the connection to the searcher
     * could not be established
     */
    private void notifySearcher() throws NotifySearcherException {
        try {
            URL url = searcherUpdatePath.toURL();
            url.openStream();
        } catch (IOException ex) {
            throw new NotifySearcherException("Could not connect to searcher at the configured address: " + searcherUpdatePath + "\n"
                    + ex.toString());
        }
    }

    /**
     * executes the code analysis for the given file
     *
     * @throws CodeAnalyzerPluginException if the source code of one of the
     * files could not be analyzed
     */
    private void executeCodeAnalysisForFile(FileDto fileDto) throws DatabaseAccessException, CodeAnalyzerPluginException {

        try {
            String fileType = MimeTypeUtil.guessMimeTypeViaFileEnding(fileDto.getFilePath());
            if (!fileType.equals(MimeTypeUtil.UNKNOWN)) {
                CodeAnalyzerPlugin plugin;
                if (!caPlugins.containsKey(fileType)) {
                    caPlugins.put(fileType, pluginLoader.getPlugin(CodeAnalyzerPlugin.class, fileType));
                }
                plugin = caPlugins.get(fileType);
                LOG.debug("Analyzing file: " + fileDto.getFilePath());
                plugin.analyzeFile(new String(fileDto.getContent()));
                AstNode ast = plugin.getAst();
                List<String> typeDeclarations = plugin.getTypeDeclarations();
                List<Usage> usages = plugin.getUsages();
                List<String> imports = plugin.getImports();
                // add the externalLinks to the FileDto, so they
                // can be parsed after the regular indexing is finished
                // write the AST information into the database
                dba.setAnalysisDataForFile(fileDto.getFilePath(), fileDto.getRepository().getName(), ast, usages, typeDeclarations, imports);
            }
        } catch (PluginLoaderException ex) {
            //in case no plugin is found for the file just skip it
        }
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
            Index index = currentPlugin.getRegularCaseAnalyzer() != null ? Field.Index.ANALYZED : Field.Index.NOT_ANALYZED;
            Field regularField = new Field(currentFieldName, fieldValue, store, index);

            doc.add(regularField);

            if (currentPlugin.getLowerCaseAnalyzer() != null) {
                Field lowerCaseField = new Field(currentFieldName + IndexConstants.LC_POSTFIX, fieldValue.toLowerCase(), store, index);
                doc.add(lowerCaseField);
            }
        }
    }

    /**
     * Initializes Lucene IndexWriter, loads plugins etc..
     */
    private void init() throws InvalidIndexLocationException, IOException {
        try {
            indexDirectory = FSDirectory.open(indexLocation);
        } catch (IOException ex) {
            throw new InvalidIndexLocationException("Cannot access index directory at: " + indexLocation);
        }

        // By default, fields are indexed case insensitive
        IndexWriterConfig config = new IndexWriterConfig(IndexConstants.LUCENE_VERSION, caseInsensitiveAnalyzer);
        indexWriter = new IndexWriter(indexDirectory, config);
        LOG.debug("IndexWriter initialization successful: " + indexLocation.getAbsolutePath());
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
    private void addFileToIndex(FileDto file) throws VersionControlPluginException, CorruptIndexException, IOException, LuceneFieldValueException {
        if (indexWriter == null) {
            LOG.error("Creation of indexDirectory failed due to missing initialization of IndexWriter!");
            throw new IllegalStateException("IndexWriter was not initialized: fatal error");
        }
        Document doc = new Document();
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

    /**
     * Removes the specified files from the index.
     */
    private void deleteFilesFromIndex(Set<FileIdentifier> files) {
        try {
            for (FileIdentifier file : files) {
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
     * Checks whether the current file is on the list of files that will not be
     * indexed
     */
    private boolean shouldFileBeIndexed(FileIdentifier file) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void setJob(IndexingJob job) {
        this.job = job;
    }
}
