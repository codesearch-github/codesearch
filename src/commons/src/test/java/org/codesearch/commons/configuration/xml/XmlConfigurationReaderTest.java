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

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.configuration.xml.dto.IndexingTaskType;
import org.codesearch.commons.configuration.xml.dto.JobDto;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
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
        RepositoryDto repo1 = getTestRepo();

        List result = configReader.getRepositories();
        assert (repo1.equals(result.get(0)));
    }

    private RepositoryDto getTestRepo() {
        List<String> ignFileNames1 = new LinkedList<String>();
        List<String> repoGroups1 = new LinkedList<String>();
        repoGroups1.add("group1");

        ignFileNames1.add("*.svn*");
        ignFileNames1.add("*.class");
        ignFileNames1.add("*.o");
        ignFileNames1.add("*.bin");

        RepositoryDto repo1 = new RepositoryDto("jdownloader-repo",
                "svn://svn.jdownloader.org/jdownloader",
                new NoAuthentication(), true,
                "SVN", ignFileNames1, new LinkedList<String>(), repoGroups1);
        return repo1;
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
        RepositoryDto repo1 = getTestRepo();
        System.out.println(repo1.getBlacklistEntries());

        JobDto job1 = new JobDto();
        job1.setCronExpression("0 * * * * ?");
        job1.getTasks().add(IndexingTaskType.INDEX);

        List<JobDto> result = configReader.getJobs();
        assert (result.get(0).equals(job1));
    }

    @Test
    public void testGetRepositoryByName() throws Exception {
        RepositoryDto repo1 = getTestRepo();
        RepositoryDto repo2 = configReader.getRepositoryByName("jdownloader-repo");
        assertEquals(repo1, repo2);
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
