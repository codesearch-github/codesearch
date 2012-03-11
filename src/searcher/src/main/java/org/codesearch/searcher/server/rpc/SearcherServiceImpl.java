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
package org.codesearch.searcher.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;
import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.configuration.dto.RepositoryDto;
import org.codesearch.commons.configuration.properties.PropertiesManager;
import org.codesearch.commons.database.DBAccess;
import org.codesearch.commons.database.DatabaseAccessException;
import org.codesearch.commons.database.DatabaseEntryNotFoundException;
import org.codesearch.commons.plugins.PluginLoader;
import org.codesearch.commons.plugins.PluginLoaderException;
import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.ExternalUsage;
import org.codesearch.commons.plugins.codeanalyzing.ast.Usage;
import org.codesearch.commons.plugins.highlighting.HighlightingPlugin;
import org.codesearch.commons.plugins.highlighting.HighlightingPluginException;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldPlugin;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldPluginLoader;
import org.codesearch.commons.plugins.vcs.FileIdentifier;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;
import org.codesearch.commons.plugins.vcs.VersionControlPluginException;
import org.codesearch.commons.utils.mime.MimeTypeUtil;
import org.codesearch.searcher.client.rpc.SearcherService;
import org.codesearch.searcher.server.DocumentSearcherImpl;
import org.codesearch.searcher.server.InvalidIndexException;
import org.codesearch.searcher.shared.*;

/**
 * Service used for search operations.
 *
 * @author Samuel Kogler
 */
@Singleton
public class SearcherServiceImpl extends RemoteServiceServlet implements SearcherService {

    private static final long serialVersionUID = 1389141189614933738L;
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(SearcherServiceImpl.class);
    /**
     * The document searcher used to search the index.
     */
    private DocumentSearcherImpl documentSearcher;
    private ConfigurationReader configurationReader;
    private PropertiesManager propertiesManager;
    private PluginLoader pluginLoader;
    private DBAccess dba;
    private List<String> repositories;
    private List<String> repositoryGroups;
    private LuceneFieldPluginLoader luceneFieldPluginLoader;

    @Inject
    public SearcherServiceImpl(DocumentSearcherImpl documentSearcher, ConfigurationReader configurationReader,
            PropertiesManager propertiesManager, PluginLoader pluginLoader, DBAccess dba,
            LuceneFieldPluginLoader luceneFieldPluginLoader) {
        this.documentSearcher = documentSearcher;
        this.propertiesManager = propertiesManager;
        this.configurationReader = configurationReader;
        this.pluginLoader = pluginLoader;
        this.dba = dba;
        this.luceneFieldPluginLoader = luceneFieldPluginLoader;
        repositoryGroups = configurationReader.getRepositoryGroups();
        repositories = new LinkedList<String>();
        for (RepositoryDto dto : configurationReader.getRepositories()) {
            if (dto != null) {
                repositories.add(dto.getName());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SearchResultDto> doSearch(String query, boolean caseSensitive, SearchType searchType, Set<String> selection, int maxResults) throws SearcherServiceException {
        List<SearchResultDto> resultItems = new LinkedList<SearchResultDto>();
        try {
            //TODO fix incompatibility
            if (searchType == SearchType.REPOSITORIES) {
                resultItems = documentSearcher.search(query, caseSensitive, selection, new HashSet<String>(), maxResults);
            } else if (searchType == SearchType.REPOSITORY_GROUPS) {
                resultItems = documentSearcher.search(query, caseSensitive, new HashSet<String>(), selection, maxResults);
            }
        } catch (ParseException ex) {
            throw new SearcherServiceException("Invalid search query: \n" + ex);
        } catch (IOException ex) {
            throw new SearcherServiceException("Exception searching the index: \n" + ex);
        } catch (InvalidIndexException ex) {
            throw new SearcherServiceException("Invalid index: \n" + ex);
        }
        return resultItems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileDto getFile(String repoName, String filePath, boolean highlight, boolean insertCodeNavigationLinks) throws SearcherServiceException {
        LOG.info("Retrieving file: " + filePath + " @ " + repoName);
        FileDto file = new FileDto();
        String indexedRevision = propertiesManager.getValue(repoName);
        try {
            RepositoryDto repositoryDto = configurationReader.getRepositoryByName(repoName);
            VersionControlPlugin vcPlugin = pluginLoader.getPlugin(VersionControlPlugin.class, repositoryDto.getVersionControlSystem());

            vcPlugin.setRepository(repositoryDto);
            FileIdentifier fileIdentifier = new FileIdentifier();
            fileIdentifier.setFilePath(filePath);
            fileIdentifier.setRepository(repositoryDto);
            org.codesearch.commons.plugins.vcs.FileDto vcFile = vcPlugin.getFileDtoForFileIdentifierAtRevision(fileIdentifier, indexedRevision);

            // GET OUTLINE IF EXISTING
            try {
                String analyzedRevision = dba.getLastAnalyzedRevisionOfRepository(repoName);
                if (analyzedRevision.equals(indexedRevision)) {
                    AstNode fileNode = dba.getBinaryIndexForFile(filePath, repoName);
                    if (fileNode != null) {
                        List<OutlineNode> outline = new LinkedList<OutlineNode>();
                        for (AstNode a : fileNode.getChildNodes()) {
                            if (a == null) {
                                continue;
                            }
                            outline.add(convertAstNodeToOutlineNode(a));
                        }
                        file.setOutline(outline);
                    } else {
                        LOG.debug("No AST could be found, disabling code navigation links.");
                        insertCodeNavigationLinks = false;
                    }
                } else {
                    // in case the last analyzed revision does not match the last indexed revision
                    LOG.warn("Code analysis data for repository " + repoName + " is not at the same revision as the index, disabling code navigation.");
                    insertCodeNavigationLinks = false;
                }
            } catch (DatabaseAccessException ex) {
                insertCodeNavigationLinks = false;
                LOG.error("Could not access database: \n" + ex);
            }

            String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
            String guessedMimeType = MimeTypeUtil.guessMimeTypeViaFileEnding(fileName);

            LOG.debug("detected mime is: " + guessedMimeType);
            file.setBinary(MimeTypeUtil.isBinaryType(guessedMimeType));

            if (MimeTypeUtil.UNKNOWN.equals(guessedMimeType)) {
                MagicMatch magicMatch;
                try {
                    magicMatch = Magic.getMagicMatch(vcFile.getContent());
                    LOG.debug("File type detected via Magic detection: " + magicMatch.getMimeType());
                    if (magicMatch.getMimeType().startsWith("text/")) {
                        file.setBinary(false);
                    }
                } catch (Exception ex) {
                    LOG.debug("Magic file type detection unsuccessful.");
                }
            }

            byte[] fileContent = vcFile.getContent().clone();
            String escapeStartToken = "";
            String escapeEndToken = "";
            String processedFileContent = "";
            if (highlight) {
                try {
                    HighlightingPlugin hlPlugin = null;
                    hlPlugin = pluginLoader.getPlugin(HighlightingPlugin.class, guessedMimeType);
                    escapeStartToken = hlPlugin.getEscapeStartToken();
                    escapeEndToken = hlPlugin.getEscapeEndToken();
                    if (insertCodeNavigationLinks) {
                        fileContent = addUsageLinksToFileContent(fileContent, filePath, repoName, escapeStartToken, escapeEndToken);
                    }
                    processedFileContent = hlPlugin.parseToHtml(fileContent, guessedMimeType);
                } catch (PluginLoaderException ex) {
                    LOG.debug("No suitable highlighting plugin found");
                    //If anything went wrong, discard changes and escape to HTML
                    processedFileContent = StringEscapeUtils.escapeHtml(new String(vcFile.getContent()));
                } catch (HighlightingPluginException ex) {
                    LOG.error(ex);
                    //If anything went wrong, discard changes and escape to HTML
                    processedFileContent = StringEscapeUtils.escapeHtml(new String(vcFile.getContent()));
                }
                file.setFileContent(processedFileContent);
            } else if (insertCodeNavigationLinks) {
                file.setFileContent(new String(addUsageLinksToFileContent(fileContent, filePath, repoName, escapeStartToken, escapeEndToken)));
            } else {
                file.setFileContent(StringEscapeUtils.escapeHtml(new String(vcFile.getContent())));
            }

        } catch (VersionControlPluginException ex) {
            throw new SearcherServiceException("Could not get file: \n" + ex);
        } catch (PluginLoaderException ex) {
            throw new SearcherServiceException("Problem loading plugin: \n" + ex);
        }
        LOG.debug("Finished retrieving file content for file: " + filePath + " @ " + repoName);
        return file;
    }

    @Override
    public JumpLocation resolveUsage(int usageId, String repository, String filePath) throws SearcherServiceException {
        LOG.debug("Looking up usage: " + usageId + " in file: " + filePath + "@" + repository);
        try {
            ExternalUsage usage = dba.getUsageForIdInFile(usageId, filePath, repository);
            String targetFilePath = getFilePathOfDeclaration(repository, usage.getTargetNodeName(), filePath);
            if (targetFilePath != null) {
                int targetLineNumber = 1;
                AstNode ast = dba.getBinaryIndexForFile(targetFilePath, repository);
                if (ast == null) {
                    LOG.warn("AST not found for file: " + targetFilePath + "@" + repository);
                } else {
                    targetLineNumber = usage.findTargetLineNumber(ast);
                    if (targetLineNumber < 1) {
                        targetLineNumber = 1;
                    }
                }
                JumpLocation location = new JumpLocation();
                location.setFilePath(targetFilePath);
                location.setRepository(repository);
                location.setLineNumber(targetLineNumber);
                return location;
            }
        } catch (DatabaseAccessException ex) {
            LOG.error(ex);
        }
        return null;
    }

//TODO: Implement this properly
//    private String parseToHtml(String input, String escapeStartToken, String escapeEndToken) {
//        String[] outerParts = input.split(escapeStartToken);
//        for (String outerPart : outerParts) {
//            String[] innerParts = outerPart.split(escapeEndToken);
//            for (String innerPart : innerParts) {
//
//            }
//        }
//        return "";
//    }
    private String getFilePathOfDeclaration(String repository, String className, String originFilePath) throws DatabaseAccessException {
        List<String> fileImports = dba.getImportsForFile(originFilePath, repository);
        String targetFilePath;
        List<String> asteriskImports = new LinkedList<String>();
        boolean importMatch = false;
        for (String currentImport : fileImports) {
            if (currentImport.endsWith("." + className)) {
                className = currentImport;
                importMatch = true;
            } else if (currentImport.endsWith("*")) {
                asteriskImports.add(currentImport);
            }
        }
        if (!importMatch) {
            targetFilePath = dba.getFilePathForTypeDeclaration(className, repository, asteriskImports);
        } else {
            targetFilePath = dba.getFilePathForTypeDeclaration(className, repository);
        }

        return targetFilePath;
    }

    @Override
    public SearchViewData getSearchViewData() throws SearcherServiceException {
        List<SearchField> searchFields = new LinkedList<SearchField>();
        for (LuceneFieldPlugin p : luceneFieldPluginLoader.getAllLuceneFieldPlugins()) {
            searchFields.add(new SearchField(p.getFieldName(), ""));
        }
        return new SearchViewData(repositories, repositoryGroups, searchFields);
    }

    private OutlineNode convertAstNodeToOutlineNode(AstNode astNode) {
        if (astNode.showInOutline()) {
            OutlineNode outlineNode = new OutlineNode();
            outlineNode.setName(astNode.getOutlineName());
            outlineNode.setStartLine(astNode.getStartLine());
            outlineNode.setCssClasses(astNode.getModifiers());
            List<SidebarNode> childs = new LinkedList<SidebarNode>();
            for (AstNode a : astNode.getChildNodes()) {
                if (a == null) {
                    continue;
                }
                childs.add(convertAstNodeToOutlineNode(a));
            }
            outlineNode.setChilds(childs);
            return outlineNode;
        } else {
            return null;
        }
    }

    private byte[] addUsageLinksToFileContent(byte[] fileContentBytes, String filePath, String repository, String hlEscapeStartToken, String hlEscapeEndToken) {
        try {
            List<Usage> usages = dba.getUsagesForFile(filePath, repository);
            if (usages == null) {
                //in case there is no entry for the filePath it is a file that has not been analyzed
                return fileContentBytes;
            }
            String resultString = "";
            String[] contentLines = new String(fileContentBytes).split("\n");
            int usageIndex = 0;
            outer:
            for (int lineNumber = 1; lineNumber <= contentLines.length; lineNumber++) {
                String currentLine = contentLines[lineNumber - 1];
                while (usageIndex < usages.size()) {
                    Usage currentUsage = usages.get(usageIndex);
                    if (currentUsage.getStartLine() == lineNumber) {
                        int startColumn = currentUsage.getStartColumn();
                        String preamble = currentLine.substring(0, startColumn - 1); //-1
                        String javaScriptEvent = "";
                        if (currentUsage instanceof ExternalUsage) {
                            javaScriptEvent = "goToUsage(" + usageIndex + ");";
                        } else {
                            javaScriptEvent = "goToLine(" + currentUsage.getReferenceLine() + ");";
                        }
                        String anchorBegin = hlEscapeStartToken + "<a class='testLink' onclick='" + javaScriptEvent + "'>" + hlEscapeEndToken;
                        String anchorEnd = hlEscapeStartToken + "</a>" + hlEscapeEndToken;
                        String remainingLine = currentLine.substring(startColumn - 1 + currentUsage.getReplacedString().length());
                        currentLine = preamble + anchorBegin + currentUsage.getReplacedString() + anchorEnd + remainingLine;
                        usageIndex++;
                    } else {
                        resultString += currentLine + "\n";
                        continue outer;
                    }
                }
                resultString += currentLine + "\n";
            }
            resultString = resultString.substring(0, resultString.length() - 1); //Truncates the last \n char
            return resultString.getBytes();
        } catch (DatabaseAccessException ex) {
            LOG.error(ex);
        }
        return fileContentBytes;
    }
}
