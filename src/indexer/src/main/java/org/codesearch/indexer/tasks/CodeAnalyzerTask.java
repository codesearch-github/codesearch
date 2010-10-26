/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.indexer.tasks;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.codesearch.commons.configuration.properties.PropertiesManager;
import org.codesearch.commons.configuration.xml.XmlConfigurationReader;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.plugins.PluginLoader;
import org.codesearch.commons.plugins.PluginLoaderException;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPlugin;
import org.codesearch.commons.plugins.codeanalyzing.ast.FileNode;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;
import org.codesearch.commons.plugins.vcs.VersionControlPluginException;
import org.codesearch.indexer.exceptions.TaskExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author David Froehlich
 */
@Deprecated
public class CodeAnalyzerTask implements Task {

    /** The dto holding the repository information */
    private RepositoryDto repository;
    /** The IndexingTask to be processed */
    private Set<String> fileNames;
    /* Instantiate a logger  */
    private static final Logger LOG = Logger.getLogger(CodeAnalyzerTask.class);
    /** The used PropertyReader */
    private PropertiesManager propertiesReader;
    /** The XmlConfigurationReader used to get the configuration */
    @Autowired
    private XmlConfigurationReader configReader;
    /** The plugin loader. */
    @Autowired
    private PluginLoader pluginLoader;
    /** The Version control Plugin */
    private VersionControlPlugin versionControlPlugin;
    
    private CodeAnalyzerPlugin codeAnalyzerPlugin;

    @Override
    public void execute() throws TaskExecutionException {
//        try {
//            String indexLocation = null;
//            LOG.info("Starting execution of indexing task");
//            indexLocation = configReader.getSingleLinePropertyValue("index-location");
//            // Read the index status file
//            propertiesReader = new PropertiesManager(indexLocation + File.separator + "revisions.properties"); //TODO add propertiesReader path
//            // Get the version control plugins
//            versionControlPlugin = pluginLoader.getPlugin(VersionControlPlugin.class, repository.getVersionControlSystem());
//            versionControlPlugin.setRepository(new URI(repository.getUrl()), repository.getUsername(), repository.getPassword());
//            fileNames = versionControlPlugin.getPathsForChangedFilesSinceRevision(propertiesReader.getPropertyFileValue(repository.getName()));
//            //TODO retrieve mime type from file and check which plugin to use
//
//            //hardcoded section, is to be removed
//            codeAnalyzerPlugin = pluginLoader.getPlugin(CodeAnalyzerPlugin.class, "JAVA");
//            Map<String, FileNode> asts = codeAnalyzerPlugin.getAstForRepository(fileNames, repository);
//            //end of hardcoded section
//
//            //TODO add handling for ast
//
//            throw new UnsupportedOperationException("Not supported yet.");
//        } catch (URISyntaxException ex) {
//        } catch (VersionControlPluginException ex) {
//        } catch (PluginLoaderException ex) {
//        } catch (IOException ex) {
//        } catch (ConfigurationException ex) {
//            //TODO make exception handling
//        }
    }
}
