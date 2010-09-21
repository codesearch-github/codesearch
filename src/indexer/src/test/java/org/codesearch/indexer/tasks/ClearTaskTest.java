/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.indexer.tasks;

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
public class ClearTaskTest {

    public ClearTaskTest() {
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
     * Test of getRepositoryName method, of class ClearTask.
     */
    @Test
    public void testGetRepositoryName() {
        System.out.println("getRepositoryName");
        ClearTask instance = new ClearTask();
        String expResult = "";
        String result = instance.getRepositoryName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setRepositoryName method, of class ClearTask.
     */
    @Test
    public void testSetRepositoryName() {
        System.out.println("setRepositoryName");
        String repositoryName = "";
        ClearTask instance = new ClearTask();
        instance.setRepositoryName(repositoryName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of execute method, of class ClearTask.
     */
    @Test
    public void testExecute() throws Exception {
        System.out.println("execute");
        ClearTask instance = new ClearTask();
        instance.execute();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}