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