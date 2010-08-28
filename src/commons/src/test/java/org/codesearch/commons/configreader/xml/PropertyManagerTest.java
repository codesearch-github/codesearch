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

package org.codesearch.commons.configreader.xml;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.GenericXmlContextLoader;
import java.util.LinkedList;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.codesearch.commons.configreader.xml.dto.RepositoryDto;
import static org.junit.Assert.*;

/**
 *
 * @author David Froehlich
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=GenericXmlContextLoader.class, locations={"classpath:org/codesearch/commons/CodesearchCommonsBeans.xml"})
public class PropertyManagerTest {
    @Autowired
    private PropertyManager propertyManager;

    public PropertyManagerTest() {
    }

    @Before
    public void setUp() {
        propertyManager.setConfigpath("/home/david/codesearch/src/commons/src/test/resources");
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getRepositories method, of class PropertyManager.
     */
    @Test
    public void testGetRepositories() throws Exception {
        //TODO update unit test
//        System.out.println("getRepositories");
//        RepositoryDto bean1 = new RepositoryDto("testRepo1", true, true);
//        RepositoryDto bean2 = new RepositoryDto("testRepo2", true, false);
//        RepositoryDto bean3 = new RepositoryDto("testRepo3", false, false);
//
//        List result = propertyManager.getRepositories();
//        assertTrue(bean1.equals(result.get(0)));
//        assertTrue(bean2.equals(result.get(1)));
//        assertTrue(bean3.equals(result.get(2)));
    }

    /**
     * Test of getSingleLinePropertyValue method, of class PropertyManager.
     */
    @Test
    public void testGetSingleLinePropertyValue() throws Exception {
        System.out.println("getSingleLinePropertyValue");
        String key = "testproperty";
        String expResult = "asdf";
        String result = propertyManager.getSingleLinePropertyValue(key);
        assertEquals(expResult, result);
    }

    /**
     * Test of getConfigpath method, of class PropertyManager.
     */
    @Test
    public void testGetConfigpath() {
        System.out.println("getConfigpath");
        String expResult = "/home/david/codesearch/src/commons/src/test/resources";
        String result = propertyManager.getConfigpath();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetSingleLinePropertyValueList() throws Exception {
        System.out.println("getSingleLinePropertyValueList");
        List<String> expResult = new LinkedList<String>();
        expResult.add("1");
        expResult.add("2");
        expResult.add("3");
        List<String> result = propertyManager.getSingleLinePropertyValueList("listTest");
        assertEquals(expResult.get(0), result.get(0));
        assertEquals(expResult.get(1), result.get(1));
        assertEquals(expResult.get(2), result.get(2));
    }

    public PropertyManager getPropertyManager() {
        return propertyManager;
    }

    public void setPropertyManager(PropertyManager propertyManager) {
        this.propertyManager = propertyManager;
    }
}