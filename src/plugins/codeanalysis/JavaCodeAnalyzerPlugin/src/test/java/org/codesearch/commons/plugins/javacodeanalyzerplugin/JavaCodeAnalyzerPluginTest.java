/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.javacodeanalyzerplugin;

import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.ClassNode;
import org.apache.log4j.Logger;
import java.util.List;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPluginException;
import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.Visibility;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.ExternalClassOrEnumUsage;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.FileNode;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.MethodNode;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author david
 */
public class JavaCodeAnalyzerPluginTest {
    /* Logger */

    private static final Logger LOG = Logger.getLogger(JavaCodeAnalyzerPluginTest.class);
    private String repository = "test";

    public JavaCodeAnalyzerPluginTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    //all analysis tests
    @Test
    public void testBasicAnalysis() throws CodeAnalyzerPluginException {
        JavaCodeAnalyzerPlugin instance = new JavaCodeAnalyzerPlugin();
        
        //create mock source code
        String content = "";
        content += "package foopackage;\n";
        content += "\n";
        content += "public class Foo {\n";
        content += "  public Foo(){\n";
        content += "  }\n";
        content += "}\n";
        
        //create expected result
        FileNode expResult = new FileNode();
        
        ClassNode classNode = new ClassNode();
        classNode.setStartLine(3);
        classNode.setStartPositionInLine(1);
        classNode.setName("Foo");
        classNode.setVisibility(Visibility.PUBLIC);
        
        MethodNode constructor = new MethodNode();
        constructor.setStartLine(4);
        constructor.setConstructor(true);
        constructor.setName("Foo");
        constructor.setVisibility(Visibility.PUBLIC);
        constructor.setStartPositionInLine(3);
        classNode.getMethods().add(constructor);
        expResult.getClasses().add(classNode);
        instance.analyzeFile(content);
        FileNode result = (FileNode) instance.getAst();
        
        assertEquals(expResult, result);
        
    }

    /**
     * Test of parseLineNumberAndFileNameOfUsage method, of class JavaCodeAnalyzerPlugin.
     */
    @Test
    public void testParseLineNumberAndFileNameOfUsage() throws Exception {
        System.out.println("parseLineNumberAndFileNameOfUsage");
        ExternalClassOrEnumUsage usage = null;
        String repository = "";
        List<String> fileImports = null;
        String originFilePath = "";
        JavaCodeAnalyzerPlugin instance = new JavaCodeAnalyzerPlugin();
        instance.parseLineNumberAndFileNameOfUsage(usage, repository, fileImports, originFilePath);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of analyzeFile method, of class JavaCodeAnalyzerPlugin.
     */
    @Test
    public void testAnalyzeFile() throws Exception {
        System.out.println("analyzeFile");
        String fileContent = "";
        JavaCodeAnalyzerPlugin instance = new JavaCodeAnalyzerPlugin();
        instance.analyzeFile(fileContent);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPurposes method, of class JavaCodeAnalyzerPlugin.
     */
    @Test
    public void testGetPurposes() {
        System.out.println("getPurposes");
        JavaCodeAnalyzerPlugin instance = new JavaCodeAnalyzerPlugin();
        String expResult = "";
        String result = instance.getPurposes();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getVersion method, of class JavaCodeAnalyzerPlugin.
     */
    @Test
    public void testGetVersion() {
        System.out.println("getVersion");
        JavaCodeAnalyzerPlugin instance = new JavaCodeAnalyzerPlugin();
        String expResult = "";
        String result = instance.getVersion();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTypeDeclarations method, of class JavaCodeAnalyzerPlugin.
     */
    @Test
    public void testGetTypeDeclarations() throws Exception {
        System.out.println("getTypeDeclarations");
        JavaCodeAnalyzerPlugin instance = new JavaCodeAnalyzerPlugin();
        List expResult = null;
        List result = instance.getTypeDeclarations();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUsages method, of class JavaCodeAnalyzerPlugin.
     */
    @Test
    public void testGetUsages() throws Exception {
        System.out.println("getUsages");
        JavaCodeAnalyzerPlugin instance = new JavaCodeAnalyzerPlugin();
        List expResult = null;
        List result = instance.getUsages();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getImports method, of class JavaCodeAnalyzerPlugin.
     */
    @Test
    public void testGetImports() {
        System.out.println("getImports");
        JavaCodeAnalyzerPlugin instance = new JavaCodeAnalyzerPlugin();
        List expResult = null;
        List result = instance.getImports();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAst method, of class JavaCodeAnalyzerPlugin.
     */
    @Test
    public void testGetAst() {
        System.out.println("getAst");
        JavaCodeAnalyzerPlugin instance = new JavaCodeAnalyzerPlugin();
        AstNode expResult = null;
        AstNode result = instance.getAst();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
