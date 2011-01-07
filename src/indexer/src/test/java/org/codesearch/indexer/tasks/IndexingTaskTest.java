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

import org.apache.commons.configuration.ConfigurationException;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.LinkedList;
import org.codesearch.commons.configuration.properties.PropertiesManager;
import org.codesearch.commons.configuration.xml.XmlConfigurationReader;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.plugins.PluginLoader;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPlugin;
import org.codesearch.commons.plugins.vcs.FileDto;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.GenericXmlContextLoader;

/**
 *
 * @author David Froehlich
 * @author Samuel Kogler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = GenericXmlContextLoader.class, locations = {"classpath:org/codesearch/commons/CommonsBeans.xml", "classpath:org/codesearch/indexer/IndexerBeans.xml"})
public class IndexingTaskTest {

    private IndexingTask task = new IndexingTask();
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private PluginLoader pluginLoader;

    public IndexingTaskTest() {
    }

    @Test
    public void testFileIsOnIgnoreList() {
        List<String> ignoredFileNames = new LinkedList<String>();
        ignoredFileNames.add("*.xml");
        ignoredFileNames.add("*test*");
        ignoredFileNames.add("*/test/*");
        task.setRepository(new RepositoryDto(null, null, null, null, false, null, ignoredFileNames, null));
        assert (task.fileIsOnIgnoreList("asdf.xml"));
        assert (task.fileIsOnIgnoreList("asdftestasdf"));
        assert (task.fileIsOnIgnoreList("/test/fasdf"));
        assertFalse(task.fileIsOnIgnoreList("asdf.txt"));
    }

    /**
     * Test of execute method, of class IndexingTask.
     */
    @Test
    public void testExecute() throws Exception {
//        XmlConfigurationReader pm = new XmlConfigurationReader();
//        List<RepositoryDto> repos = pm.getRepositories();
//        for (RepositoryDto repo : repos) {
//            if (!repo.getVersionControlSystem().equals("FILESYSTEM")) {
//                IndexingTask t = (IndexingTask) applicationContext.getBean("indexingTask");
//                t.setRepository(repo);
//                t.setCodeAnalysisEnabled(true);
//                t.execute();
//            }
//        }
    }

    @Test
    public void testCodeAnalysis() throws Exception {
        RepositoryDto repo = new RepositoryDto();
        repo.setVersionControlSystem("FILESYSTEM");
        CodeAnalyzerPlugin codeAnalyzerPlugin = pluginLoader.getPlugin(CodeAnalyzerPlugin.class, "java");
        VersionControlPlugin versionControlPlugin = pluginLoader.getPlugin(VersionControlPlugin.class, "FILESYSTEM");
        FileDto fileDto = versionControlPlugin.getFileForFilePath("/home/daasdingo/workspace/codesearch/src/commons/src/main/java/org/codesearch/commons/plugins/PluginLoader.java");
        codeAnalyzerPlugin.analyzeFile(new String(fileDto.getContent()));
    }

    @Test
    public void testExecuteLocal() throws Exception {
        XmlConfigurationReader pm = new XmlConfigurationReader();
        List<RepositoryDto> repos = pm.getRepositories();

        ClearTask clear = (ClearTask) applicationContext.getBean("clearTask");
        clear.execute();
        
        for (RepositoryDto repo : repos) {
            if (repo.getVersionControlSystem().equals("FILESYSTEM")) {
                repo.setUrl(repo.getUrl().replace("$home", System.getProperty("user.home")));
                IndexingTask t = (IndexingTask) applicationContext.getBean("indexingTask");
           //     pr.setPropertyFileValue(repo.getName(), "0");
                t.setRepository(repo);
                t.setCodeAnalysisEnabled(true);
                t.execute();
            }
        }
    }

    /**
     * Test of createIndex method, of class IndexingTask.
     */
    @Test
    public void testCreateIndex() throws Exception {
//        System.out.println("createIndex");
//        IndexingTask instance = new IndexingTask();
//        boolean expResult = false;
//        boolean result = instance.createIndex();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of setRepository method, of class IndexingTask.
     */
    @Test
    public void testSetRepository() {
//        System.out.println("setRepository");
//        RepositoryDto repository = null;
//        IndexingTask instance = new IndexingTask();
//        instance.setRepository(repository);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
}
