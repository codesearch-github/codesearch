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

import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.configuration.xml.XmlConfigurationReader;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.database.ConnectionPool;
import org.codesearch.commons.database.ConnectionPoolImpl;
import org.codesearch.commons.database.DBAccess;
import org.codesearch.commons.database.DBAccessImpl;
import org.codesearch.commons.plugins.PluginLoader;
import org.codesearch.commons.plugins.PluginLoaderImpl;
import org.junit.Test;

/**
 *
 * @author David Froehlich
 * @author Samuel Kogler
 */
public class IndexingTaskTest {


    public IndexingTaskTest() {
    }

    @Test
    public void testExecute() throws Exception {
        //TODO write this test
    }

    @Test
    public void testExecuteLocal() throws Exception {
        ConfigurationReader configReader = new XmlConfigurationReader(null);
        
        PluginLoader pl = new PluginLoaderImpl();
        ConnectionPool cp = new ConnectionPoolImpl(configReader);
        DBAccess dba = new DBAccessImpl(cp);
        IndexingTask t = new IndexingTask(configReader, dba, pl);
        t.setIndexLocation("/tmp/test/");
        RepositoryDto repo = configReader.getRepositoryByName("svn_local");
        repo.setUrl(repo.getUrl().replace("$home", System.getProperty("user.home")));
        t.setRepository(repo);
        t.setCodeAnalysisEnabled(false);
        t.execute();
    }
}
