/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.vcs;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class SubversionPluginTest {

    SubversionPlugin sp = new SubversionPlugin();

    public SubversionPluginTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        try {
            try {
                sp.setRepository(new URI("svn://portal.htl-kaindorf.at/svnsearch"), "feldruebe", "dota!123");
            } catch (URISyntaxException ex) {
                Logger.getLogger(SubversionPluginTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (VersionControlPluginException ex) {
            fail("The repository could not be opened");
        }
        
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getPathsForChangedFilesSinceRevision method, of class SubversionPlugin.
     */
    @Test
    public void testGetPathsForChangedFilesSinceRevision() throws Exception {
        System.out.println("getPathsForChangedFilesSinceRevision");
        String revision = "218";
       // sp.setRepository(new URI("svn://portal.htl-kaindorf.at/svnsearch"), "feldruebe", "dota!123");
        Set result = sp.getPathsForChangedFilesSinceRevision(revision);
        Iterator iter = result.iterator();
        String s = (String) iter.next();
        assert (s.endsWith("testfile.txt"));
    }

    /**
     * Test of getFileContentForFilePath method, of class SubversionPlugin.
     */
    @Test
    public void testGetFileContentForFilePath() throws Exception {
        System.out.println("getFileContentForFilePath");
        String filePath = "/svnsearch/trunk/src/main/java/com/bearingpoint/ta/svnsearch/testfile.txt";
        String result = sp.getFileContentForFilePath(filePath);
        assert (result.equals("test"));
    }
}
