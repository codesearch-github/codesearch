/**
 * Copyright 2010 David Froehlich   <david.froehlich@businesssoftware.at>,
 *                Samuel Kogler     <samuel.kogler@gmail.com>,
 *                Stephan Stiboller <stistc06@htlkaindorf.at>
 *
 * This file is part of Codesearch.
 *
 * Codesearch is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Codesearch is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Codesearch.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.CompoundNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.Usage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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

    @Test
    public void testSetAnalysisDataForFile() throws Exception {
        String filePath = "asdf";
        String repository = "svn_local";
        List<String> types = new LinkedList<String>();
        types.add("testType1");
        types.add("org.blargh.testType2");
        Object usages = new LinkedList();
        DBAccess.setAnalysisDataForFile(filePath, repository, usages, usages, types);
    }

    /**
     * Test of setBinaryIndexForFile method, of class DBUtils.
     */
    @Test
    public void testSerializeObject() throws Exception {
        String filePath = "asdf";
        String repository = "svn_local";
        //dummy object, dunno why i picked arraylist
        List<Usage> usages = new LinkedList<Usage>();
        usages.add(new Usage(0, 0, 0, 0, ""));
//        DBAccess.setAnalysisDataForFile(filePath, repository, usages, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
      //  assert (List<Usage>)(DBAccess.getBinaryIndexForFile(filePath, repository)) != null;
    }

    /**
     * Test of setupConnections method, of class DBUtils.
     */
    @Test
    public void testSetupConnections() throws Exception {
//        System.out.println("setupConnections");
//        DBUtils.setupConnections();
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
//        fail("The test case is a prototype.");
    }

    /**
     * Test of ensureThatRecordExists method, of class DBUtils.
     */
    @Test
    public void testEnsureThatRecordExists() throws Exception {
        System.out.println("ensureThatRecordExists");
        String filePath = "asdf";
        String repository = "svn_local";
        System.out.println(DBAccess.ensureThatRecordExists(filePath, repository));
    }
}
