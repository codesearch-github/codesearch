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

package org.codesearch.commons.configuration.properties;

import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author Stephan Stiboller
 */
//TODO spring
public class PropertiesManagerTest {

    /**
     * The used PropertiesReader instance
     */
    private PropertiesManager pr;

    public PropertiesManagerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws IOException {
        pr = new PropertiesManager(System.getProperty("user.home") + "/workspace/svnsearch/revisions.properties");
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

    
}
