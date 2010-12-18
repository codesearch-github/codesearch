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
        BufferedReader br = new BufferedReader(new FileReader("/home/david/codesearch/src/indexer/src/main/java/org/codesearch/indexer/tasks/IndexingTask.java"));

        while(br.ready()){
            fileContent += br.readLine()+"\n";
        }
//        System.out.println(fileContent);
        RepositoryDto repo = new RepositoryDto("test", "test", "test", "test", true, "SVN", null, null);
        new JavaCodeAnalyzerPlugin().analyzeFile(fileContent, repo);
    }
}
