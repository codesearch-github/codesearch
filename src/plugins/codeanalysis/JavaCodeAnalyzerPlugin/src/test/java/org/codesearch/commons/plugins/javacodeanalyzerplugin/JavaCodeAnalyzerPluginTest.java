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

import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPluginException;
import org.codesearch.commons.plugins.codeanalyzing.ast.Visibility;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.ClassNode;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.FileNode;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.MethodNode;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author david
 */
public class JavaCodeAnalyzerPluginTest {
    /* Logger */
    
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

    @Test
    public void testMultiLineMethodCall() throws CodeAnalyzerPluginException {
        
    }
}
