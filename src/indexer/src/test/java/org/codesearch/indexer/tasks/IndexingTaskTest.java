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

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.configuration.dto.NoAuthentication;
import org.codesearch.commons.configuration.dto.RepositoryDto;
import org.codesearch.commons.configuration.xml.XmlConfigurationReader;
import org.codesearch.commons.database.DBAccess;
import org.codesearch.commons.plugins.PluginLoader;
import org.codesearch.commons.plugins.PluginLoaderImpl;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldPluginLoaderImpl;
import org.codesearch.indexer.server.tasks.ClearTask;
import org.codesearch.indexer.server.tasks.IndexingTask;
import org.junit.Test;

/**
 *
 * @author David Froehlich
 * @author Samuel Kogler
 */
public class IndexingTaskTest {

    private File testIndexDir = new File("/tmp/test");
    private String testRepoDir = "src/test/resources/testrepo";

    public IndexingTaskTest() {
        testIndexDir.mkdir();
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
                new LinkedList<String>(), "0");

        List<RepositoryDto> repos = new LinkedList<RepositoryDto>();
        repos.add(repo);

        ClearTask c = new ClearTask(dba);
        c.setIndexLocation(testIndexDir);
        c.execute();

        IndexingTask t = new IndexingTask(dba, pl, null, new LuceneFieldPluginLoaderImpl(pl));
        t.setIndexLocation(testIndexDir);
        t.setRepositories(repos);
        t.execute();
    }

    @Test
    public void cleanup() {
        try {
            FileUtils.deleteDirectory(testIndexDir);
        } catch (IOException ex) {
            System.out.println("Deleting index files failed");
        }
    }
}
