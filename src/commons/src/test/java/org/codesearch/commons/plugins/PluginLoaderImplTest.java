/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins;

import java.util.List;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPlugin;
import org.codesearch.commons.plugins.highlighting.HighlightingPlugin;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldPlugin;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author David Froehlich
 */
public class PluginLoaderImplTest {
    private PluginLoaderImpl pl = new PluginLoaderImpl();
    public PluginLoaderImplTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getPlugin method, of class PluginLoaderImpl.
     */
    @Test
    public void testGetPlugin() throws Exception {
        //TODO remove test for final release
        pl.getPlugin(VersionControlPlugin.class, "BZR");
        pl.getPlugin(VersionControlPlugin.class, "SVN");
     //   pl.getPlugin(VersionControlPlugin.class, "GIT");
        pl.getPlugin(VersionControlPlugin.class, "FILESYSTEM");
        pl.getMultiplePluginsForPurpose(LuceneFieldPlugin.class, "lucene_field_plugin");
        pl.getPlugin(HighlightingPlugin.class, "text/x-java-source");
        pl.getPlugin(CodeAnalyzerPlugin.class, "text/x-java-source");
        pl.getPlugin(CodeAnalyzerPlugin.class, "application/xml");
    }
}