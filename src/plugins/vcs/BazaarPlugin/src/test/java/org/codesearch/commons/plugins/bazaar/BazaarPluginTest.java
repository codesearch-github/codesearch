/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.bazaar;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.plugins.vcs.VersionControlPluginException;
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
public class BazaarPluginTest {

    BazaarPlugin plugin = new BazaarPlugin();

    public BazaarPluginTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws URISyntaxException, VersionControlPluginException {
        String url = "bzr+ssh://bazaar.launchpad.net/%2Bbranch/codesearch/";
        plugin.setRepository(new URI(url), "", "");
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getFileContentForFilePath method, of class BazaarPlugin.
     */
   // @Test
    public void testGetFileContentForFilePath() throws Exception {
        System.out.println("getFileContentForFilePath");
        String filePath = "bzr+ssh://bazaar.launchpad.net/%2Bbranch/codesearch//src/searcher/src/test/java/SearcherInterfaceTest.java";
        byte[] result = plugin.getFileContentForFilePath(filePath);
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
        String revision = "100";
        boolean expResult = false;
        Set result = plugin.getChangedFilesSinceRevision(revision);
        if(result != null)
            expResult=true;
        assertTrue(expResult);

        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }


  
}
