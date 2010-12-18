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
package org.codesearch.searcher.server.rpc;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;
import org.codesearch.commons.configuration.xml.XmlConfigurationReader;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.plugins.PluginLoader;
import org.codesearch.commons.plugins.PluginLoaderException;
import org.codesearch.commons.plugins.highlighting.HighlightingPlugin;
import org.codesearch.commons.plugins.highlighting.HighlightingPluginException;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;
import org.codesearch.commons.plugins.vcs.VersionControlPluginException;
import org.codesearch.commons.utils.MimeTypeUtil;

import org.codesearch.searcher.client.rpc.SearcherService;
import org.codesearch.searcher.server.DocumentSearcher;
import org.codesearch.searcher.shared.InvalidIndexLocationException;
import org.codesearch.searcher.shared.SearchResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.HtmlUtils;

/**
 * Service used for search operations.
 * @author Samuel Kogler
 */
public class SearcherServiceImpl extends AutowiringRemoteServiceServlet implements SearcherService {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(SearcherServiceImpl.class);
    /** The document searcher used to search the index. */
    @Autowired
    private DocumentSearcher documentSearcher;
    @Autowired
    private XmlConfigurationReader xmlConfigurationReader;
    @Autowired
    private PluginLoader pluginLoader;
    private List<String> repositories;
    private List<String> repositoryGroups;

    @Override
    protected void postConstruct() {
        try {
            repositoryGroups = xmlConfigurationReader.getRepositoryGroups();
            repositories = new LinkedList<String>();
            for (RepositoryDto dto : xmlConfigurationReader.getRepositories()) {
                if (dto != null) {
                    repositories.add(dto.getName());
                }
            }
        } catch (ConfigurationException ex) {
            LOG.error(ex);
        }
    }

    @Override
    public List<SearchResultDto> doSearch(String query, boolean caseSensitive, List<String> selectedRepositories, List<String> selectedRepositoryGroups) throws InvalidIndexLocationException {
        List<SearchResultDto> resultItems = new LinkedList<SearchResultDto>();
        try {
            resultItems = documentSearcher.search(query, caseSensitive, selectedRepositories, selectedRepositoryGroups);
        } catch (ParseException ex) {
            LOG.error("Could not parse query: " + ex);
        } catch (IOException ex) {
            LOG.error(ex);
        } catch (ConfigurationException ex) {
            LOG.error(ex);
        }
        return resultItems;
    }

    public void setDocumentSearcher(DocumentSearcher documentSearcher) {
        this.documentSearcher = documentSearcher;
    }

    public void setXmlConfigurationReader(XmlConfigurationReader xmlConfigurationReader) {
        this.xmlConfigurationReader = xmlConfigurationReader;
    }

    @Override
    public List<String> getAvailableRepositoryGroups() {
        return repositoryGroups;
    }

    @Override
    public List<String> getAvailableRepositories() {
        return repositories;
    }

    @Override
    public String getFileContent(String repository, String filePath) {
        String fileContent = "";
        try {
            String guessedMimeType = MimeTypeUtil.guessMimeTypeViaFileEnding(filePath);
            
            RepositoryDto repositoryDto = xmlConfigurationReader.getRepositoryByName(repository);
            VersionControlPlugin vcPlugin = pluginLoader.getPlugin(VersionControlPlugin.class, repositoryDto.getVersionControlSystem());
            vcPlugin.setRepository(new URI(repositoryDto.getUrl()), repositoryDto.getUsername(), repositoryDto.getPassword());
            fileContent = new String(vcPlugin.getFileContentForFilePath(filePath));

            try {
                HighlightingPlugin hlPlugin = pluginLoader.getPlugin(HighlightingPlugin.class, guessedMimeType);
                fileContent = hlPlugin.parseToHtml(fileContent, guessedMimeType);
            } catch (PluginLoaderException ex) {
                // No plugin found, just escape to HTML
                fileContent = "<pre>" + HtmlUtils.htmlEscape(fileContent) + "</pre>";
            }
        } catch (HighlightingPluginException ex) {
            LOG.error(ex);
        } catch (URISyntaxException ex) {
            LOG.error(ex);
        } catch (VersionControlPluginException ex) {
            LOG.error(ex);
        } catch (ConfigurationException ex) {
            LOG.error(ex);
        } catch (PluginLoaderException ex) {
            LOG.error(ex);
        }

        return fileContent;
    }
}
