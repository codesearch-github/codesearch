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
package org.codesearch.searcher.server.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
     /* Logger */
    private static final Logger LOG = Logger.getLogger(STAutocompleterTest.class);
    
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
            LOG.info(STAutocompleterTest.class.getName());
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
        LOG.info("autocomplete examples");
        String term = "class";
        ag.setupIndex(realIndex, defaultField);
        List<String> result = ag.suggest(term);
        LOG.info("count: " + result.size());
        for(String r : result)
        {
            LOG.info(" ++ " + r);
        }
        assertTrue(true);
    }
}
