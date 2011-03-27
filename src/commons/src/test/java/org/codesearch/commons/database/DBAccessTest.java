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

package org.codesearch.commons.database;

import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

import org.junit.Test;

/**
 * Tests {@link DBAccessImpl}
 * @author Samuel Kogler
 * @author David Froehlich
 */
public class DBAccessTest {


    public DBAccessTest() {
    }

    @Test
    public void testSetAnalysisDataForFile() throws Exception {
//        String filePath = "asdf";
//        String repository = "svn_local";
//        List<String> types = new LinkedList<String>();
//        List<String> imports = new LinkedList<String>();
//        AstNode mockAstNode = new MockAstNode();
//        List<Usage> usages = new LinkedList<Usage>();
//
//        dbAccess.setAnalysisDataForFile(filePath, repository, mockAstNode, usages, types, imports);
//        dbAccess.getBinaryIndexForFile(filePath, repository);
    //FIXME the whole architecture is so fucked up that its impossible to test properly
    }

    /**
     * Test of setBinaryIndexForFile method, of class DBUtils.
     */
    @Test
    public void testSerializeObject() throws Exception {
//        String filePath = "asdf";
//        String repository = "svn_local";
//        List<Usage> usages = new LinkedList<Usage>();
//        usages.add(new Usage(0, 0, 0, 0, ""));
//        dbAccess.setAnalysisDataForFile(filePath, repository, usages, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
      //  assert (List<Usage>)(DBAccessImpl.getBinaryIndexForFile(filePath, repository)) != null;
    }

    /**
     * Test of getLastAnalyzedRevisionOfRepository method, of class DBUtils.
     */
    @Test
    public void testGetLastAnalyzedRevisionOfRepository() throws Exception {
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
//        String repositoryName = "";
//        DBUtils.createRepositoryEntry(repositoryName);
//        fail("The test case is a prototype.");
    }

    /**
     * Test of setLastAnalyzedRevisionOfRepository method, of class DBUtils.
     */
    @Test
    public void testSetLastAnalyzedRevisionOfRepository() throws Exception {
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
//        String filePath = "asdf";
//        String repository = "svn_local";
//        System.out.println(dbAccess.ensureThatRecordExists(filePath, repository));
    }
}
