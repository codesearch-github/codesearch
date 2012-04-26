/*
 *
 *
 */
package org.codesearch.commons.configuration.xml;

import java.io.File;

import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.configuration.ConfigurationValidator;
import org.codesearch.commons.configuration.InvalidConfigurationException;
import org.codesearch.commons.configuration.dto.RepositoryDto;
import org.codesearch.commons.plugins.ExceptionThrowingPluginLoader;
import org.codesearch.commons.plugins.NullPluginLoader;
import org.codesearch.commons.plugins.PluginLoader;
import org.junit.Test;

/**
 *
 * @author Samuel Kogler
 */
public class ConfigurationValidatorTest {

    @Test
    public void testInvalidConfiguration() {
        //assert that the error handling prevents loading an incomplete repository configuraion
        try {
            ConfigurationReader configReader = new XmlConfigurationReader("codesearch_config_invalid_repository_configuration.xml");
            PluginLoader pluginLoader = new NullPluginLoader();
            ConfigurationValidator configurationValidator = new ConfigurationValidator(configReader, pluginLoader);
            assert (false);
        } catch (InvalidConfigurationException ex) {
            //in case the exception is thrown everything is fine
        }
    }

    @Test
    public void testInvalidDirectories() {
        //assert that the error handling prevents loading an incomplete repository configuraion
        try {
            MockConfigurationReader configReader = new MockConfigurationReader();
            configReader.configuration.setCacheDirectory(new File("/asdf"));
            configReader.configuration.setIndexLocation(new File("/sadfsad"));

            PluginLoader pluginLoader = new NullPluginLoader();
            ConfigurationValidator configurationValidator = new ConfigurationValidator(configReader, pluginLoader);
            assert (false);
        } catch (InvalidConfigurationException ex) {
            //in case the exception is thrown everything is fine
        }
    }

    @Test
    public void testUnnamedRepo() {
        //assert that the error handling prevents loading an incomplete repository configuraion
        try {
            MockConfigurationReader configReader = new MockConfigurationReader();
            configReader.configuration.setCacheDirectory(new File("/tmp"));
            configReader.configuration.setIndexLocation(new File("/tmp"));
            RepositoryDto repo = new RepositoryDto();
            repo.setUrl("sdf");
            repo.setVersionControlSystem("TESTVCS");
            configReader.configuration.getRepositories().add(repo);

            PluginLoader pluginLoader = new NullPluginLoader();
            ConfigurationValidator configurationValidator = new ConfigurationValidator(configReader, pluginLoader);
            assert (false);
        } catch (InvalidConfigurationException ex) {
            //in case the exception is thrown everything is fine
        }
    }

    @Test
    public void testNoVcs() {
        //assert that the error handling prevents loading an incomplete repository configuraion
        try {
            MockConfigurationReader configReader = new MockConfigurationReader();
            configReader.configuration.setCacheDirectory(new File("/tmp"));
            configReader.configuration.setIndexLocation(new File("/tmp"));
            RepositoryDto repo = new RepositoryDto();
            repo.setName("asdf");
            repo.setUrl("sdf");
            configReader.configuration.getRepositories().add(repo);

            PluginLoader pluginLoader = new NullPluginLoader();
            ConfigurationValidator configurationValidator = new ConfigurationValidator(configReader, pluginLoader);
            assert (false);
        } catch (InvalidConfigurationException ex) {
            //in case the exception is thrown everything is fine
        }
    }

    @Test
    public void testInvalidVcs() {
        //assert that the error handling prevents loading an incomplete repository configuraion
        try {
            MockConfigurationReader configReader = new MockConfigurationReader();
            configReader.configuration.setCacheDirectory(new File("/tmp"));
            configReader.configuration.setIndexLocation(new File("/tmp"));
            RepositoryDto repo = new RepositoryDto();
            repo.setName("asdf");
            repo.setUrl("sdf");
            repo.setVersionControlSystem("testvcs");
            configReader.configuration.getRepositories().add(repo);

            PluginLoader pluginLoader = new ExceptionThrowingPluginLoader();
            ConfigurationValidator configurationValidator = new ConfigurationValidator(configReader, pluginLoader);
            assert (false);
        } catch (InvalidConfigurationException ex) {
            //in case the exception is thrown everything is fine
        }
    }

    @Test
    public void testInvalidRegex() {
        //assert that the error handling prevents loading an incomplete repository configuraion
        try {
            MockConfigurationReader configReader = new MockConfigurationReader();
            configReader.configuration.setCacheDirectory(new File("/tmp"));
            configReader.configuration.setIndexLocation(new File("/tmp"));
            RepositoryDto repo = new RepositoryDto();
            repo.setName("asdf");
            repo.setUrl("sdf");
            repo.setVersionControlSystem("testvcs");
            repo.getBlacklistEntries().add("/(((");
            configReader.configuration.getRepositories().add(repo);
            PluginLoader pluginLoader = new NullPluginLoader();
            ConfigurationValidator configurationValidator = new ConfigurationValidator(configReader, pluginLoader);
            assert (false);
        } catch (InvalidConfigurationException ex) {
            //in case the exception is thrown everything is fine
        }
    }
}
