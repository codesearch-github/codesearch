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
package org.codesearch.commons.configuration.xml;

import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.configuration.xml.dto.JobDto;
import java.util.GregorianCalendar;
import org.apache.commons.configuration.ConfigurationException;
import java.util.LinkedList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.codesearch.commons.configuration.xml.dto.TaskDto;
import static org.junit.Assert.*;

/**
 *
 * @author David Froehlich
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(loader = GenericXmlContextLoader.class, locations = {"classpath:org/codesearch/commons/CodesearchCommonsBeans.xml"})
public class XmlConfigurationReaderTest {

    //   TODO use spring injection
    //   @Autowired
    private XmlConfigurationReader configReader = new XmlConfigurationReader();

    public XmlConfigurationReaderTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getRepositories method, of class XmlConfigurationReader.
     */
    @Test
    public void testGetRepositories() throws Exception {
        // TODO update unit test
        System.out.println("getRepositories");
        List<String> ignFileNames1 = new LinkedList<String>();
        List<String> repoGroups1 = new LinkedList<String>();
        repoGroups1.add("group1");
        repoGroups1.add("group2");

        ignFileNames1.add("*.xml");
        ignFileNames1.add("*.jpg");
        ignFileNames1.add("*.txt");
        ignFileNames1.add("*.PNG");
        ignFileNames1.add("*.png");
        ignFileNames1.add("*img*");
        ignFileNames1.add("*.svn*");
        ignFileNames1.add("*.class*");
        

        RepositoryDto repo1 = new RepositoryDto("svnsearch_repo", "svn://portal.htl-kaindorf.at/svnsearch", "feldruebe", "dota!123", true, "SVN", ignFileNames1, repoGroups1);

        List<String> ignFileNames2 = new LinkedList<String>();
        List<String> repoGroups2 = new LinkedList<String>();
        repoGroups2.add("group1");
        ignFileNames2.add("*.jpg");
        ignFileNames2.add("*.txt");
        ignFileNames2.add("*.PNG");
        ignFileNames2.add("*.png");
        ignFileNames2.add("*img*");
        ignFileNames2.add("*.svn*");
        ignFileNames2.add("*.class*");
        RepositoryDto repo2 = new RepositoryDto("svn_local", System.getProperty("user.home")+"/workspace/svnsearch", "null", "null", true, "FILESYSTEM", ignFileNames2, repoGroups2);

        List result = configReader.getRepositories();
        assertTrue(repo1.equals(result.get(0)));
        assertTrue(repo2.equals(result.get(1)));
    }

    /**
     * Test of getSingleLinePropertyValue method, of class XmlConfigurationReader.
     */
    @Test
    public void testGetSingleLinePropertyValue() throws Exception {
        System.out.println("getSingleLinePropertyValue");
        String key = "testproperty";
        String expResult = "asdf";
        String result = configReader.getSingleLinePropertyValue(key);
        assertEquals(expResult, result);
    }

    /**
     * Test of getTasks method, of class XmlConfigurationReader.
     */
    @Test
    public void testGetJobs() throws ConfigurationException {
        System.out.println("getJobs");
        JobDto job1 = new JobDto();

        List<String> ignFileNames1 = new LinkedList<String>();
        List<String> repoGroups1 = new LinkedList<String>();
        repoGroups1.add("group1");
        repoGroups1.add("group2");

        ignFileNames1.add("*.xml");
        ignFileNames1.add("*.jpg");
        ignFileNames1.add("*.txt");
        ignFileNames1.add("*.PNG");
        ignFileNames1.add("*.png");
        ignFileNames1.add("*img*");
        ignFileNames1.add("*.svn*");
        ignFileNames1.add("*.class*");
        RepositoryDto repo1 = new RepositoryDto("svnsearch_repo", "svn://portal.htl-kaindorf.at/svnsearch", "feldruebe", "dota!123", true, "SVN", ignFileNames1, repoGroups1);

        List<String> ignFileNames2 = new LinkedList<String>();
        List<String> repoGroups2 = new LinkedList<String>();
        repoGroups2.add("group1");
        ignFileNames2.add("*.jpg");
        ignFileNames2.add("*.txt");
        ignFileNames2.add("*.PNG");
        ignFileNames2.add("*.png");
        ignFileNames2.add("*img*");
        ignFileNames2.add("*.svn*");
        ignFileNames2.add("*.class*");
        RepositoryDto repo2 = new RepositoryDto("svn_local", System.getProperty("user.home")+"/workspace/svnsearch", "null", "null", true, "FILESYSTEM", ignFileNames2, repoGroups2);

        job1.getTasks().add(new TaskDto(repo1, TaskDto.TaskType.index));
        job1.setInterval(60);
        job1.setStartDate(new GregorianCalendar(2010, 7, 13, 18, 0));
        JobDto job2 = new JobDto();

        job2.getTasks().add(new TaskDto(null, TaskDto.TaskType.clear));

        job2.setInterval(10080);
        job2.setStartDate(new GregorianCalendar(2010, 7, 11, 18, 0));

        List<JobDto> result = configReader.getJobs();
        assert (result.size() == 2);
        assert (result.get(0).equals(job1));
        assert (result.get(1).equals(job2));
    }

    @Test
    public void testGetRepositoryByName() throws Exception {
        System.out.println("getRepositoryByName");
        List<String> ignoredFiles = new LinkedList<String>();
        List<String> repoGroups = new LinkedList<String>();
        ignoredFiles.add("*.xml");
        ignoredFiles.add("*.jpg");
        ignoredFiles.add("*.txt");
        RepositoryDto expResult = new RepositoryDto("testRepo1", "http://test.org", "testUser", "testPassword", true, "SVN", ignoredFiles, repoGroups);
        RepositoryDto result = configReader.getRepositoryByName("testRepo1");
    }

    @Test
    public void testGetSingleLinePropertyValueList() throws Exception {
        System.out.println("getSingleLinePropertyValueList");
        List<String> expResult = new LinkedList<String>();
        expResult.add("1");
        expResult.add("2");
        expResult.add("3");
        List<String> result = configReader.getSingleLinePropertyValueList("listTest");
        assertEquals(expResult.get(0), result.get(0));
        assertEquals(expResult.get(1), result.get(1));
        assertEquals(expResult.get(2), result.get(2));
    }
}
