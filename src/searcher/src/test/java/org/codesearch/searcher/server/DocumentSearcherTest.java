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
package org.codesearch.searcher.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.configuration.ConfigurationException;
import org.codesearch.commons.configuration.xml.XmlConfigurationReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author David Froehlich
 */
public class DocumentSearcherTest {

    private DocumentSearcher instance;

    public DocumentSearcherTest() {
        try {
            instance = new DocumentSearcher(XmlConfigurationReader.getInstance());
        } catch (ConfigurationException ex) {
            Logger.getLogger(DocumentSearcherTest.class.getName()).log(Level.SEVERE, null, ex);
        }

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
     * Test of search method, of class DocumentSearcher.
     */
    @Test
    public void testSearch() throws Exception {
        System.out.println("search");
        String searchString = "SVN_NAME";
        List result = instance.search(searchString, true, new LinkedList<String>(), new LinkedList<String>());
        assertFalse(result.isEmpty());
        searchString = "svn_name";
        result = instance.search(searchString, true, new LinkedList<String>(), new LinkedList<String>());
        assertTrue(result.isEmpty());
        searchString = "svn_name";
        result = instance.search(searchString, false, new LinkedList<String>(), new LinkedList<String>());
        assertFalse(result.isEmpty());
        searchString = "svn_name";
        List<String> repoGroups = new LinkedList<String>();
        repoGroups.add("group1");
        result = instance.search(searchString, false, new LinkedList<String>(), repoGroups);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testParseQuery() throws Exception{
        System.out.println("parseQuery");
        String searchTerm = "asdf";
        List<String> repositories = new LinkedList<String>();
        repositories.add("testRepo1");
        repositories.add("testRepo2");
        repositories.add("testRepo3");
        String expectedResult = "content:\"asdf\" AND (repository:testRepo1 OR repository:testRepo2 OR repository:testRepo3)";
        String result = instance.parseQuery(searchTerm, true, repositories, new LinkedList<String>());
        System.out.println(result);
        System.out.println(expectedResult);
        assertEquals(expectedResult, result);
        List<String> repositoryGroups = new LinkedList<String>();
        repositoryGroups.add("group1");
        expectedResult = "content:\"asdf\" AND (repository:svnsearch_repo OR repository:svn_local)";
        result = instance.parseQuery(searchTerm, true, new LinkedList<String>(), repositoryGroups);
        assertEquals(expectedResult, result);
    }
}
