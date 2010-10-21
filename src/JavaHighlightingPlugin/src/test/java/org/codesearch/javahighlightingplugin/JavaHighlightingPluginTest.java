/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.javahighlightingplugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import junit.framework.TestCase;

/**
 *
 * @author David Froehlich
 */
public class JavaHighlightingPluginTest extends TestCase {
    JavaHighlightingPlugin plugin = new JavaHighlightingPlugin();
    public JavaHighlightingPluginTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of parseToHtml method, of class JavaHighlightingPlugin.
     */
    public void testParseToHtml() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(new File(System.getProperty("user.home")+"/workspace/test/test.java")));
        String input = "";
        while(br.ready()){
            input += br.readLine() + "\n";
        }
        String result = plugin.parseToHtml(input);
        System.out.println(result);
    }
}
