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

package org.codesearch.commons.plugins.javacodeanalyzerplugin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import junit.framework.TestCase;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;

/**
 *
 * @author David Froehlich
 */
public class JavaCodeAnalyzerPluginTest extends TestCase {
    
    public JavaCodeAnalyzerPluginTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

//    /**
//     * Test of getAstForCurrentFile method, of class JavaCodeAnalyzerPlugin.
//     */
//    public void testGetAstForCurrentFile() throws Exception {
//        System.out.println("getAstForCurrentFile");
//        JavaCodeAnalyzerPlugin instance = new JavaCodeAnalyzerPlugin();
//        Map expResult = null;
//        Map result = instance.getAstForCurrentFile();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of analyzeFile method, of class JavaCodeAnalyzerPlugin.
     */
    public void testAnalyzeFile() throws Exception {
        System.out.println("analyzeFile");
        String fileContent = "";
        BufferedReader br = new BufferedReader(new FileReader("/home/david/codesearch/src/indexer/src/main/java/org/codesearch/indexer/tasks/test.java"));

        while(br.ready()){
            fileContent += br.readLine()+"\n";
        }
//        System.out.println(fileContent);
        RepositoryDto repo = new RepositoryDto("test", "test", "test", "test", true, "SVN", null, null);
        new JavaCodeAnalyzerPlugin().analyzeFile(fileContent, repo);
    }
}
