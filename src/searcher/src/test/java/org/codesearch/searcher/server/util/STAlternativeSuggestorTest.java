/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.searcher.server.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
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
public class STAlternativeSuggestorTest {

    public STAlternativeSuggestorTest() {
    }

    public String spellIndexString  = "/tmp/spelltest/";
    public String realIndexString = "/tmp/test/";
    public String defaultField  = "content";
    public Directory spellIndex;
    public Directory realIndex;
    public STAlternativeSuggestor sta;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        try {
            spellIndex = FSDirectory.open(new File(spellIndexString));
            realIndex = FSDirectory.open(new File(realIndexString));
            sta = new STAlternativeSuggestor(defaultField, spellIndex);
            sta.createSpellIndex(defaultField, realIndex, spellIndex);
            
            
        } catch (IOException ex) {
            Logger.getLogger(STAlternativeSuggestorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of suggest method, of class STAlternativeSuggestor.
     */
    @Test
    public void testSuggest() throws Exception {
        System.out.println("suggest test");
        String queryString = "class" ;
        List<String> result = sta.suggest(queryString);
        for(String r : result)
        {
            System.out.println(" -- " + r);
        }
        assertTrue(true);
    }


}