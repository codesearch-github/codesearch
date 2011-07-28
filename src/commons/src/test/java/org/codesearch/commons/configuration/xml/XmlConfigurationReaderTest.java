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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.configuration.xml.dto.JobDto;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.configuration.xml.dto.TaskDto;
import org.codesearch.commons.plugins.vcs.BasicAuthentication;
import org.codesearch.commons.plugins.vcs.NoAuthentication;
import org.junit.Before;
import org.junit.Test;

public class XmlConfigurationReaderTest {

    private ConfigurationReader configReader;

    public XmlConfigurationReaderTest() {
    }

    @Before
    public void setUp() {
        configReader = new XmlConfigurationReader("");
    }

    @Test
    public void testGetRepositories() throws Exception {
        List<String> ignFileNames1 = new LinkedList<String>();
        List<String> repoGroups1 = new LinkedList<String>();
        repoGroups1.add("group1");
        repoGroups1.add("group2");

        ignFileNames1.add("*.xml");
        ignFileNames1.add("*.jpg");
        ignFileNames1.add("*img*");
        ignFileNames1.add("*.svn*");
        ignFileNames1.add("*.class*");

        RepositoryDto repo1 = new RepositoryDto("svnsearch_repo", "http://portal.htl-kaindorf.at/svnsearch", new BasicAuthentication("feldruebe", "dota!123"), true, "SVN", ignFileNames1, new LinkedList<String>(), repoGroups1);

        List<String> ignFileNames2 = new LinkedList<String>();
        List<String> repoGroups2 = new LinkedList<String>();
        ignFileNames2.add("*.jpg");
        ignFileNames2.add("*img*");
        ignFileNames2.add("*generated*");
        ignFileNames2.add("*.svn*");
        ignFileNames2.add("*.class*");
        RepositoryDto repo2 = new RepositoryDto("local_repo", System.getProperty("user.home") + "/workspace/svnsearch", new NoAuthentication(), true, "FILESYSTEM", ignFileNames2, new LinkedList<String>(), repoGroups2);

        List result = configReader.getRepositories();
        assertTrue(repo1.equals(result.get(0)));
        assertTrue(repo2.equals(result.get(1)));
    }

    @Test
    public void testGetValue() throws Exception {
        String key = "testproperty";
        String expResult = "asdf";
        String result = configReader.getValue(key);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetJobs() throws ConfigurationException {
        List<String> ignFileNames1 = new LinkedList<String>();
        List<String> repoGroups1 = new LinkedList<String>();
        repoGroups1.add("group1");
        repoGroups1.add("group2");

        ignFileNames1.add("*.xml");
        ignFileNames1.add("*.jpg");
        ignFileNames1.add("*img*");
        ignFileNames1.add("*.svn*");
        ignFileNames1.add("*.class*");

        RepositoryDto repo1 = new RepositoryDto("svnsearch_repo", "http://portal.htl-kaindorf.at/svnsearch", new BasicAuthentication("feldruebe", "dota!123"), true, "SVN", ignFileNames1, new LinkedList<String>(), repoGroups1);

        System.out.println(repo1.getBlacklistEntries());

        JobDto job1 = new JobDto();
        job1.setCronExpression("*/10 * * * *");
        job1.getTasks().add(new TaskDto(repo1, TaskDto.TaskType.index, true));

        List<JobDto> result = configReader.getJobs();
        assert (result.get(0).equals(job1));
    }

    @Test
    public void testGetRepositoryByName() throws Exception {
        List<String> ignoredFiles = new LinkedList<String>();
        List<String> repoGroups = new LinkedList<String>();
        ignoredFiles.add("*.xml");
        ignoredFiles.add("*.jpg");
        ignoredFiles.add("*.txt");
        RepositoryDto expResult = new RepositoryDto("testRepo1", "http://test.org", new NoAuthentication(), true, "SVN", ignoredFiles, new LinkedList<String>(), repoGroups);
        RepositoryDto result = configReader.getRepositoryByName("testRepo1");
    }

    @Test
    public void testGetValueList() throws Exception {
        List<String> expResult = new LinkedList<String>();
        expResult.add("1");
        expResult.add("2");
        expResult.add("3");
        List<String> result = configReader.getValueList("listTest");
        assertEquals(expResult.get(0), result.get(0));
        assertEquals(expResult.get(1), result.get(1));
        assertEquals(expResult.get(2), result.get(2));
    }
}
