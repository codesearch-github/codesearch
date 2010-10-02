/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.indexer.tasks;

import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.LinkedList;
import java.io.File;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.codesearch.commons.configuration.properties.PropertiesManager;
import org.codesearch.commons.configuration.xml.XmlConfigurationReader;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
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
@ContextConfiguration(loader = GenericXmlContextLoader.class, locations = {"classpath:org/codesearch/indexer/IndexerBeans.xml"})
public class IndexingTaskTest {

    private IndexingTask task = new IndexingTask();
    @Autowired
    private ApplicationContext applicationContext;

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
        task.setRepository(new RepositoryDto(null, null, null, null, false, null, ignoredFileNames));
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
        XmlConfigurationReader pm = new XmlConfigurationReader();
        List<RepositoryDto> repos = pm.getRepositories();
        PropertiesManager pr = new PropertiesManager("/tmp/test/revisions.properties");

        for (RepositoryDto repo : repos) {
            if (!repo.getVersionControlSystem().equals("FILESYTEM")) {
                IndexingTask t = (IndexingTask) applicationContext.getBean("indexingTask");
                pr.setPropertyFileValue(repo.getName(), "0");
                t.setRepository(repo);
                t.execute();
            }
        }
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
                t.execute();
            }
        }
    }

    /**
     * Test of initializeIndexWriter method, of class IndexingTask.
     */
    @Test
    public void testInitializeIndexWriter() {
        System.out.println("initializeIndexWriter");
        Analyzer luceneAnalyzer = new StandardAnalyzer(Version.LUCENE_30);
        File dir = null;
        task.initializeIndexWriter(luceneAnalyzer, dir);
    }

    /**
     * Test of createIndex method, of class IndexingTask.
     */
    @Test
    public void testCreateIndex() throws Exception {
        System.out.println("createIndex");
        IndexingTask instance = new IndexingTask();
        boolean expResult = false;
        boolean result = instance.createIndex();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setRepository method, of class IndexingTask.
     */
    @Test
    public void testSetRepository() {
        System.out.println("setRepository");
        RepositoryDto repository = null;
        IndexingTask instance = new IndexingTask();
        instance.setRepository(repository);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
