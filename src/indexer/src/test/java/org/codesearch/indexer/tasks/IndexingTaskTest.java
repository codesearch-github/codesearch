/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.indexer.tasks;

import org.codesearch.commons.propertyreader.properties.PropertiesReader;
import org.codesearch.commons.configreader.xml.PropertyManager;
import java.util.List;
import java.util.LinkedList;
import java.io.File;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.codesearch.commons.configreader.xml.dto.RepositoryDto;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author David Froehlich
 */
public class IndexingTaskTest {

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
    public void testFileIsOnIgnoreList(){
        IndexingTask task = new IndexingTask();
        List<String> ignoredFileNames = new LinkedList<String>();
        ignoredFileNames.add("*.xml");
        ignoredFileNames.add("*test*");
        ignoredFileNames.add("*/test/*");
        task.setRepository(new RepositoryDto(null, null, null, null, false, null, ignoredFileNames));
        assert(task.fileIsOnIgnoreList("asdf.xml"));
        assert(task.fileIsOnIgnoreList("asdftestasdf"));
        assert(task.fileIsOnIgnoreList("/test/fasdf"));
        assertFalse(task.fileIsOnIgnoreList("asdf.txt"));
    }
    
    /**
     * Test of execute method, of class IndexingTask.
     */
    @Test
    public void testExecute() throws Exception {
        PropertyManager pm = new PropertyManager();
        List<RepositoryDto> repos = pm.getRepositories();
        PropertiesReader pr = new PropertiesReader("revisions.properties");
        
        for(RepositoryDto repo : repos){
            IndexingTask t = new IndexingTask();
            pr.setPropertyFileValue(repo.getName(), "0");
            t.setRepository(repo);
            t.execute();
        }
    }

    /**
     * Test of addLuceneFields method, of class IndexingTask.
     */
    @Test
    public void testAddLuceneFields() throws Exception {
        System.out.println("addLuceneFields");
        Document doc = null;
        String path = "";
        IndexingTask instance = new IndexingTask();
        Document expResult = null;
        Document result = instance.addLuceneFields(doc, path);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of initializeIndexWriter method, of class IndexingTask.
     */
    @Test
    public void testInitializeIndexWriter() {
        System.out.println("initializeIndexWriter");
        Analyzer luceneAnalyzer = null;
        File dir = null;
        IndexingTask instance = new IndexingTask();
        instance.initializeIndexWriter(luceneAnalyzer, dir);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
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
