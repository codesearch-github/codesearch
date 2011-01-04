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
        BufferedReader br = new BufferedReader(new FileReader("/home/david/workspace/svnsearch/WakMusic/src/java/servlets/AddEvent.java"));
        String input = "";
        while(br.ready()){
            input += br.readLine() + "\n";
        }

        String result = plugin.parseToHtml(input.getBytes(), MimeTypeUtil.JAVA);
        System.out.println(result);
    }
}
