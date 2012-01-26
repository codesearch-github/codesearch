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

import static org.junit.Assert.assertEquals;
import org.junit.Test;


public class PropertiesManagerTest {

    /**
     * The used PropertiesReader instance
     */
    private RepositoryRevisionManager pr = new RepositoryRevisionManager(PropertiesManagerTest.class.getClassLoader().getResourceAsStream("revisions.properties"));

    public PropertiesManagerTest() {
    }

    /**
     * Test of getValue method, of class PropertiesReader.
     */
    @Test
    public void testGetPropertyFileValue() throws Exception {
        String key = "testrepo1";
        String expResult = "5";
        String result = pr.getValue(key);
        assertEquals(expResult, result);
    }


}
