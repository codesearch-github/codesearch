/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.database;

import java.util.ArrayList;
import java.util.Random;
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
public class DBAccessTest {

    public DBAccessTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        DBAccess.setupConnections();
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
     * Test of setBinaryIndexForFile method, of class DBUtils.
     */
    @Test
    public void testSerializeObject() throws Exception {
        String filePath = "asdf";
        String repository = "svn_local";
        //dummy object, dunno why i picked arraylist
        ArrayList list = new ArrayList();
        DBAccess.setBinaryIndexForFile(filePath, repository, list);
        assert DBAccess.getBinaryIndexForFile(filePath, repository) != null;
    }

    /**
     * Test of setupConnections method, of class DBUtils.
     */
    @Test
    public void testSetupConnections() throws Exception {
//        System.out.println("setupConnections");
//        DBUtils.setupConnections();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of getLastAnalyzedRevisionOfRepository method, of class DBUtils.
     */
    @Test
    public void testGetLastAnalyzedRevisionOfRepository() throws Exception {
//        System.out.println("getLastAnalyzedRevisionOfRepository");
//        String repositoryName = "";
//        String expResult = "";
//        String result = DBUtils.getLastAnalyzedRevisionOfRepository(repositoryName);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of createRepositoryEntryInDatabase method, of class DBUtils.
     */
    @Test
    public void testCreateRepositoryEntryInDatabase() throws Exception {
//        System.out.println("createRepositoryEntryInDatabase");
//        String repositoryName = "";
//        DBUtils.createRepositoryEntry(repositoryName);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of setLastAnalyzedRevisionOfRepository method, of class DBUtils.
     */
    @Test
    public void testSetLastAnalyzedRevisionOfRepository() throws Exception {
//        System.out.println("setLastAnalyzedRevisionOfRepository");
//        String repositoryName = "";
//        String revision = "";
//        DBUtils.setLastAnalyzedRevisionOfRepository(repositoryName, revision);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of ensureThatRecordExists method, of class DBUtils.
     */
    @Test
    public void testEnsureThatRecordExists() throws Exception {
//        System.out.println("ensureThatRecordExists");
//        String filePath = "";
//        String repository = "";
//        DBUtils.ensureThatRecordExists(filePath, repository);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
}
