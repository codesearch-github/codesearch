/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.propertyreader.properties;

import java.io.IOException;
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
//TODO spring
public class PropertiesReaderTest {

    /**
     * The used PropertiesReader instance
     */
    private PropertiesReader pr;

    public PropertiesReaderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws IOException {
        pr = new PropertiesReader("/home/zeheron/workspace/codesearch/codesearch/src/indexer/src/test/resources/repository.properties");
    }

    @After
    public void tearDown() {
    }
    
    /**
     * Test of getPropertyFileValue method, of class PropertiesReader.
     */
    @Test
    public void testGetPropertyFileValue() throws Exception {
        System.out.println("getPropertyFileValue");
        String key = "testrepo1";
        String expResult = "0";
        String result = pr.getPropertyFileValue(key);
        assertEquals(expResult, result);
    }

    /**
     * Test of setPropertyFileValue method, of class PropertiesReader.
     */
    @Test
    public void testSetPropertyFileValue() throws Exception {
        System.out.println("setPropertyFileValue");
        String key = "testrepo2";
        String value = "1";
        PropertiesReader instance = null;
        pr.setPropertyFileValue(key, value);
        assertEquals(value, pr.getPropertyFileValue(key));
    }
    
}