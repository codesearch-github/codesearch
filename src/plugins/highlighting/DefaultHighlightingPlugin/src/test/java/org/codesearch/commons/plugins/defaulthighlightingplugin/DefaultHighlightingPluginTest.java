/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.defaulthighlightingplugin;

import com.uwyn.jhighlight.renderer.XmlXhtmlRenderer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Map;
import junit.framework.TestCase;
import org.codesearch.commons.utils.MimeTypeUtil;


/**
 *
 * @author David Froehlich
 */
public class DefaultHighlightingPluginTest extends TestCase {
    DefaultHighlightingPlugin plugin = new DefaultHighlightingPlugin();
    public DefaultHighlightingPluginTest(String testName) {
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
        
        Map cssStyles = XmlXhtmlRenderer.DEFAULT_CSS;
        Iterator iter = cssStyles.keySet().iterator();
        String key = (String) iter.next();
        for(; iter.hasNext(); key = (String) iter.next()){
            String value = (String) cssStyles.get(key);
            System.out.println(key + "{\n"+value+"\n}");
        }
        BufferedReader br = new BufferedReader(new FileReader(new File(System.getProperty("user.home")+"/workspace/test/test.java")));
        String input = "";
        while(br.ready()){
            input += br.readLine() + "\n";
        }
        String result = plugin.parseToHtml(input.getBytes(), MimeTypeUtil.JAVA);
        //System.out.println(result);
    }
}