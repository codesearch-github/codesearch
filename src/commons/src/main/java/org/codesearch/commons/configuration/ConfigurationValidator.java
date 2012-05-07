package org.codesearch.commons.configuration;

import com.google.inject.Inject;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.codesearch.commons.configuration.dto.RepositoryDto;
import org.codesearch.commons.constants.IndexConstants;
import org.codesearch.commons.database.DBAccess;
import org.codesearch.commons.plugins.PluginLoader;
import org.codesearch.commons.plugins.PluginLoaderException;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;

/**
 *
 * @author Samuel Kogler
 */
public class ConfigurationValidator {

    ConfigurationReader configurationReader;
    public static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ConfigurationValidator.class);
    PluginLoader pluginLoader;

    @Inject
    public ConfigurationValidator(ConfigurationReader configurationReader, PluginLoader pluginLoader) throws InvalidConfigurationException {
        this.configurationReader = configurationReader;
        this.pluginLoader = pluginLoader;
        validate();
    }

    private void validate() throws InvalidConfigurationException {
        if (!(configurationReader.getCacheDirectory().isDirectory() && configurationReader.getCacheDirectory().canWrite())) {
            throw new InvalidConfigurationException("Cache directory \"" + configurationReader.getCacheDirectory() + "\" is invalid.");
        }

        if (!(configurationReader.getIndexLocation().isDirectory() && configurationReader.getIndexLocation().canWrite())) {
            throw new InvalidConfigurationException("Index location \"" + configurationReader.getIndexLocation() + "\" is invalid.");
        }
        validateRepositories();
    }

    private void validateRepositories() throws InvalidConfigurationException {

        for (RepositoryDto repository : configurationReader.getRepositories()) {
            if (StringUtils.isEmpty(repository.getName())
                    || StringUtils.isEmpty(repository.getUrl())
                    || StringUtils.isEmpty(repository.getVersionControlSystem())) {
                throw new InvalidConfigurationException("Mandatory repository fields not specified. "
                        + "At least name, url and version-control-system are needed.");
            }

            try {
                pluginLoader.getPlugin(VersionControlPlugin.class, repository.getVersionControlSystem());
            } catch (PluginLoaderException ex) {
                throw new InvalidConfigurationException("No version control plugin found for specified purpose: "
                        + repository.getVersionControlSystem()
                        + "\nThis value was configured for repository: " + repository.getName());
            }

            for (String s : repository.getBlacklistEntries()) {
                try {
                    Pattern.compile(s);
                } catch (PatternSyntaxException ex) {
                    throw new InvalidConfigurationException("Invalid regular expression in blacklist entry: " + s + "\n" + ex);
                }
            }

            for (String s : repository.getWhitelistEntries()) {
                try {
                    Pattern.compile(s);
                } catch (PatternSyntaxException ex) {
                    throw new InvalidConfigurationException("Invalid regular expression in whitelist entry: " + s + "\n" + ex);
                }
            }
        }
    }
}
