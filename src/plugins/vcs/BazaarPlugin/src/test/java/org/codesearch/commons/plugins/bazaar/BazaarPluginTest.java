/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.bazaar;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import org.codesearch.commons.plugins.vcs.FileDto;
import org.codesearch.commons.plugins.vcs.VersionControlPluginException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class tests the functionality of the BZRPlugin
 * @author Stephan Stiboller
 * @author David Froehlich
 *
 */
public class BazaarPluginTest {

    BazaarPlugin plugin = new BazaarPlugin();
    private String localPath = "file:///home/zeheron/workspace/testground/bazaar/";

    public BazaarPluginTest() {
    }

    @Before
    public void setUp() throws URISyntaxException, VersionControlPluginException {
        plugin.setRepository(new URI(localPath), "", "");
    }
    
    /**
     * Test of getFileContentForFilePath method, of class BazaarPlugin.
     */
    @Test
    public void testGetFileContentForFilePath() throws Exception {
        System.out.println("getFileContentForFilePath");
        String filePath = "file:///home/zeheron/workspace/testground/bazaar/test.java";
        byte[] result = plugin.getFileForFilePath(filePath).getContent();
        boolean content = false;
        if(result != null)
            content = true;
        assertTrue(content);
    }

    /**
     * Test of getChangedFilesSinceRevision method, of class BazaarPlugin.
     */
    @Test
    public void testGetChangedFilesSinceRevision() throws Exception {
        System.out.println("getChangedFilesSinceRevision");
        String revision = "1";
        boolean expResult = false;
        Set<FileDto> result = plugin.getChangedFilesSinceRevision(revision);
        if(result != null)
            expResult=true;
        assertTrue(expResult);

        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }


  
}
