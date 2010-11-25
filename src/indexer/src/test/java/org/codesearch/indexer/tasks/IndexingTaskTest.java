/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.indexer.tasks;

import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.LinkedList;
import org.codesearch.commons.configuration.properties.PropertiesManager;
import org.codesearch.commons.configuration.xml.XmlConfigurationReader;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.constants.MimeTypeNames;
import org.codesearch.commons.database.DBAccess;
import org.codesearch.commons.plugins.PluginLoader;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPlugin;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.GenericXmlContextLoader;

/**
 *
 * @author David Froehlich
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = GenericXmlContextLoader.class, locations = {"classpath:org/codesearch/commons/CommonsBeans.xml", "classpath:org/codesearch/indexer/IndexerBeans.xml"})
public class IndexingTaskTest {

    private IndexingTask task = new IndexingTask();
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private PluginLoader pluginLoader;

    public IndexingTaskTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        try {
            DBAccess.setupConnections();
        } catch (ConfigurationException ex) {
            //TODO add useful error handling
        }
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testFileIsOnIgnoreList() {
        List<String> ignoredFileNames = new LinkedList<String>();
        ignoredFileNames.add("*.xml");
        ignoredFileNames.add("*test*");
        ignoredFileNames.add("*/test/*");
        task.setRepository(new RepositoryDto(null, null, null, null, false, null, ignoredFileNames,null));
        assert (task.fileIsOnIgnoreList("asdf.xml"));
        assert (task.fileIsOnIgnoreList("asdftestasdf"));
        assert (task.fileIsOnIgnoreList("/test/fasdf"));
        assertFalse(task.fileIsOnIgnoreList("asdf.txt"));
    }

    /**
     * Test of execute method, of class IndexingTask.
     */
    @Test
    public void testExecute() throws Exception {
//        XmlConfigurationReader pm = new XmlConfigurationReader();
//        List<RepositoryDto> repos = pm.getRepositories();
//        for (RepositoryDto repo : repos) {
//            if (!repo.getVersionControlSystem().equals("FILESYSTEM")) {
//                IndexingTask t = (IndexingTask) applicationContext.getBean("indexingTask");
//                t.setRepository(repo);
//                t.setCodeAnalysisEnabled(true);
//                t.execute();
//            }
//        }
    }

    @Test
    public void testCodeAnalysis() throws Exception {
        RepositoryDto repo = new RepositoryDto();
        repo.setVersionControlSystem("FILESYSTEM");
        CodeAnalyzerPlugin plugin = pluginLoader.getPlugin(CodeAnalyzerPlugin.class, "java");
        VersionControlPlugin vcPlugin = pluginLoader.getPlugin(VersionControlPlugin.class, "FILESYSTEM");
        byte[] fileContent = vcPlugin.getFileContentForFilePath("/home/david/wakmusic/trunk/Wakmusic/src/java/beans/Band.java");
        plugin.analyzeFile(new String(fileContent), repo);
     //   System.out.println(plugin.getAstForCurrentFile().getOutlineForChildElements());
    }

    @Test
    public void testExecuteLocal() throws Exception {
        XmlConfigurationReader pm = new XmlConfigurationReader();
        List<RepositoryDto> repos = pm.getRepositories();
        PropertiesManager pr = new PropertiesManager("/tmp/test/revisions.properties");

        ClearTask clear = new ClearTask();
        clear.setRepositoryName(null);

        clear.execute();
        for (RepositoryDto repo : repos) {
            if (repo.getVersionControlSystem().equals("FILESYSTEM")) {
                repo.setUrl(repo.getUrl().replace("$home", System.getProperty("user.home")));
                IndexingTask t = (IndexingTask) applicationContext.getBean("indexingTask");
                pr.setPropertyFileValue(repo.getName(), "0");
                t.setRepository(repo);
                t.setCodeAnalysisEnabled(true);
                t.execute();
            }
        }
    }

    /**
     * Test of createIndex method, of class IndexingTask.
     */
    @Test
    public void testCreateIndex() throws Exception {
//        System.out.println("createIndex");
//        IndexingTask instance = new IndexingTask();
//        boolean expResult = false;
//        boolean result = instance.createIndex();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of setRepository method, of class IndexingTask.
     */
    @Test
    public void testSetRepository() {
//        System.out.println("setRepository");
//        RepositoryDto repository = null;
//        IndexingTask instance = new IndexingTask();
//        instance.setRepository(repository);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
}
