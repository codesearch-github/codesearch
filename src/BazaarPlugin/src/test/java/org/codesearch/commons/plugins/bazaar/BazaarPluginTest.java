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
        String url = "starbuck.endofinternet.net/srv/bzr/codesearch/trunk";
        String username = "feldruebe";
        String password = "asdf123";
        plugin.setRepository(new URI(url), username, password);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of setRepository method, of class BazaarPlugin.
     */
    @Test
    public void testSetRepository() throws Exception {
//        System.out.println("setRepository");
//        URI url = null;
//        String username = "";
//        String password = "";
//        BazaarPlugin instance = new BazaarPlugin();
//        instance.setRepository(url, username, password);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of getFileContentForFilePath method, of class BazaarPlugin.
     */
    @Test
    public void testGetFileContentForFilePath() throws Exception {
//        System.out.println("getFileContentForFilePath");
//        String filePath = "";
//        BazaarPlugin instance = new BazaarPlugin();
//        byte[] expResult = null;
//        byte[] result = instance.getFileContentForFilePath(filePath);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of getChangedFilesSinceRevision method, of class BazaarPlugin.
     */
    @Test
    public void testGetChangedFilesSinceRevision() throws Exception {
        System.out.println("getChangedFilesSinceRevision");
        String revision = "0";
        Set expResult = null;
        Set result = plugin.getChangedFilesSinceRevision(revision);
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getRepositoryRevision method, of class BazaarPlugin.
     */
    @Test
    public void testGetRepositoryRevision() throws Exception {
//        System.out.println("getRepositoryRevision");
//        BazaarPlugin instance = new BazaarPlugin();
//        String expResult = "";
//        String result = instance.getRepositoryRevision();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of getPurposes method, of class BazaarPlugin.
     */
    @Test
    public void testGetPurposes() {
//        System.out.println("getPurposes");
//        BazaarPlugin instance = new BazaarPlugin();
//        String expResult = "";
//        String result = instance.getPurposes();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of getVersion method, of class BazaarPlugin.
     */
    @Test
    public void testGetVersion() {
//        System.out.println("getVersion");
//        BazaarPlugin instance = new BazaarPlugin();
//        String expResult = "";
//        String result = instance.getVersion();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
}
