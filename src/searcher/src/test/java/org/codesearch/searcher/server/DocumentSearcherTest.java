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

package org.codesearch.searcher.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.configuration.InvalidConfigurationException;
import org.codesearch.commons.configuration.xml.XmlConfigurationReader;
import org.junit.Test;

/**
 *
 * @author David Froehlich
 */
public class DocumentSearcherTest {

    private DocumentSearcherImpl instance;


    public DocumentSearcherTest() throws InvalidConfigurationException {
        ConfigurationReader configurationReader = new XmlConfigurationReader(null);
        instance = new DocumentSearcherImpl(configurationReader);
    }

    /**
     * Test of search method, of class DocumentSearcher.
     */
    @Test
    public void testSearch() throws Exception {
        String searchString = "SVN_NAME";
        List result = instance.search(searchString, true, new HashSet<String>(), new HashSet<String>(), 500);
        assertFalse(result.isEmpty());
        searchString = "svn_name";
        result = instance.search(searchString, true, new HashSet<String>(), new HashSet<String>(), 500);
        assertTrue(result.isEmpty());
        searchString = "svn_name";
        result = instance.search(searchString, false, new HashSet<String>(), new HashSet<String>(), 500);
        assertFalse(result.isEmpty());
        searchString = "svn_name";
        Set<String> repoGroups = new HashSet<String>();
        repoGroups.add("group1");
        result = instance.search(searchString, false, new HashSet<String>(), repoGroups, 500);
        assertFalse(result.isEmpty());
    }

//    @Test
//    public void testParseQuery() throws Exception {
//        String searchTerm = "asdf";
//        Set<String> repositories = new HashSet<String>();
//        repositories.add("testRepo1");
//        repositories.add("testRepo2");
//        repositories.add("testRepo3");
//        String expectedResult = "content:\"asdf\" AND (repository:testRepo1 OR repository:testRepo2 OR repository:testRepo3)";
//        String result = instance.parseQuery(searchTerm, true, repositories, new HashSet<String>());
//        assertEquals(expectedResult, result);
//        Set<String> repositoryGroups = new HashSet<String>();
//        repositoryGroups.add("group1");
//        expectedResult = "content:\"asdf\" AND (repository:svnsearch_repo OR repository:local_repo)";
//        result = instance.parseQuery(searchTerm, true, new HashSet<String>(), repositoryGroups);
//        assertEquals(expectedResult, result);
//    }
}
