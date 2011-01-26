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

package org.codesearch.commons.plugins.defaulthighlightingplugin;

import java.io.BufferedReader;
import java.io.FileReader;
import org.junit.Test;

/**
 *
 * @author David Froehlich
 */
public class DefaultHighlightingPluginTest {
    private DefaultHighlightingPlugin plugin = new DefaultHighlightingPlugin();

    /**
     * Test of parseToHtml method, of class JavaHighlightingPlugin.
     */
    @Test
    public void testParseToHtml() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("/home/david/workspace/svnsearch/WakMusic/src/java/servlets/AddEvent.java"));
        String input = "";
        while(br.ready()){
            input += br.readLine() + "\n";
        }

        String result = plugin.parseToHtml(input.getBytes(), "text/x-java-source");
        System.out.println(result);
    }
}
