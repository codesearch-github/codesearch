/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.vcs;

import java.net.URI;
import java.util.Set;
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
public class FilesystemPluginTest {

    public FilesystemPluginTest() {
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
     * Test of getFileContentForFilePath method, of class FilesystemPlugin.
     */
    @Test
    public void testGetFileContentForFilePath() throws Exception {
        FilesystemPlugin instance = new FilesystemPlugin();
        Set<FileDto> files = instance.getChangedFilesSinceRevision("0");
        FileDto file = (FileDto) files.toArray()[0];
        byte[] content =  instance.getFileContentForFilePath(file.getFilePath());
        System.out.println(content.toString());
    }
}