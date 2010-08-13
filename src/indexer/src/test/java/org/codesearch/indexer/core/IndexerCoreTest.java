/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.indexer.core;

import java.io.File;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author zeheron
 */
public class IndexerCoreTest {

    public IndexerCoreTest() {
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

    /**
     * Test of cleanIndexDirectory method, of class IndexerCore.
     */
    @Test
    public void testCleanIndexDirectory() {
        System.out.println("cleanIndexDirectory");
        File dir = null;
        IndexerCore instance = new IndexerCore();
        boolean expResult = false;
        boolean result = instance.cleanIndexDirectory(dir);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of initializeIndexWriter method, of class IndexerCore.
     */
    @Test
    public void testInitializeIndexWriter() {
        System.out.println("initializeIndexWriter");
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
        File dir = new File("/tmp/test/");
        IndexerCore instance = new IndexerCore();
        instance.initializeIndexWriter(analyzer, dir);
        assertTrue(instance.getIndexWriter() != null);
    }

    /**
     * Test of indexDirectory method, of class IndexerCore.
     */
    @Test
    public void testIndexDirectory() {
        System.out.println("indexDirectory");
        File dir = null;
        IndexerCore instance = new IndexerCore();
        boolean expResult = false;
        boolean result = instance.indexDirectory(dir);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}