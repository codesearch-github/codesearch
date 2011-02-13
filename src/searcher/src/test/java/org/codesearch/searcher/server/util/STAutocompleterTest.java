/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.searcher.server.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
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
public class STAutocompleterTest {

    public STAutocompleterTest() {
    }
    public String comDirString = "/tmp/spelltest/";
    public String realIndexString = "/tmp/test/";
    public String defaultField = "content";
    public Directory comDir;
    public Directory realIndex;
    public STAutocompleter ag;
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        try {
            comDir = FSDirectory.open(new File(comDirString));
            realIndex = FSDirectory.open(new File(realIndexString));
            ag = new STAutocompleter(comDirString);
            
        } catch (IOException ex) {
            Logger.getLogger(STAutocompleterTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of suggest method, of class STAutocompleter.
     */
    @Test
    public void testSuggest() throws Exception {
        System.out.println("autocomplete examples");
        String term = "class";
        ag.setupIndex(realIndex, defaultField);
        List<String> result = ag.suggest(term);
        System.out.println("count: " + result.size());
        for(String r : result)
        {
            System.out.println(" ++ " + r);
        }
        assertTrue(true);
    }
}
