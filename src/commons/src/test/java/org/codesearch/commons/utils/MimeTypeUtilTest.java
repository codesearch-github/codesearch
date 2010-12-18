/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.utils;

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
public class MimeTypeUtilTest {

    public MimeTypeUtilTest() {
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
     * Test of guessMimeTypeViaFileEnding method, of class MimeTypeUtil.
     */
    @Test
    public void testGuessMimeTypeViaFileEnding() {
        System.out.println("guessMimeTypeViaFileEnding");
        String fileName = "/test/testest/file.java";
        String expResult = MimeTypeUtil.JAVA;
        String result = MimeTypeUtil.guessMimeTypeViaFileEnding(fileName);
        assertEquals(expResult, result);
    }

}