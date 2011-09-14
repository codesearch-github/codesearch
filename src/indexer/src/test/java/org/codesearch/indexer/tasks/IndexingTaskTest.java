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
package org.codesearch.indexer.tasks;

import org.codesearch.indexer.server.tasks.IndexingTask;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.codesearch.commons.configuration.ConfigurationReader;


import org.codesearch.commons.configuration.xml.XmlConfigurationReader;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.database.DBAccess;
import org.codesearch.commons.plugins.PluginLoader;
import org.codesearch.commons.plugins.PluginLoaderImpl;
import org.codesearch.commons.plugins.vcs.NoAuthentication;
import org.junit.Test;

/**
 *
 * @author David Froehlich
 * @author Samuel Kogler
 */
public class IndexingTaskTest {

//    private String testIndexDir = "src/test/resources/testindex";
    private String testIndexDir = "/tmp/test";
    private String testRepoDir = "src/test/resources/testrepo";

    public IndexingTaskTest() {
        File f = new File(testIndexDir);
        f.mkdir();
    }

    @Test
    public void testExecute() throws Exception {
        //TODO write this test
    }

    @Test
    public void testExecuteLocal() throws Exception {
        ConfigurationReader configReader = new XmlConfigurationReader(null);

        PluginLoader pl = new PluginLoaderImpl(configReader);
        DBAccess dba = new MockDatabaseImpl();

        RepositoryDto repo = new RepositoryDto(
                "local_repo",
                testRepoDir,
                new NoAuthentication(),
                true,
                "FILESYSTEM",
                new LinkedList<String>(),
                new LinkedList<String>(),
                new LinkedList<String>());

        List<RepositoryDto> repos = new LinkedList<RepositoryDto>();
        repos.add(repo);

        // FIXME update this test
//        ClearTask c = new ClearTask(dba);
//        c.setIndexLocation(testIndexDir);
//        c.execute();

        IndexingTask t = new IndexingTask(dba, pl, "");
        t.setIndexLocation(testIndexDir);
        t.setRepositories(repos);
        t.execute();
    }

    @Test
    public void cleanup() {
        File ti = new File(testIndexDir);
        try {
            FileUtils.deleteDirectory(ti);
        } catch (IOException ex) {
            System.out.println("Deleting index files failed");
        }
    }
}
