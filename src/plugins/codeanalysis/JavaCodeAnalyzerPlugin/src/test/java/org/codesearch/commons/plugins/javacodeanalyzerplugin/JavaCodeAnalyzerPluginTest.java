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

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import junit.framework.TestCase;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPluginException;
import org.codesearch.commons.plugins.codeanalyzing.ast.ExternalLink;
import org.codesearch.commons.plugins.codeanalyzing.ast.ExternalUsage;
import org.codesearch.commons.plugins.codeanalyzing.ast.Usage;

/**
 *
 * @author David Froehlich
 */
public class JavaCodeAnalyzerPluginTest extends TestCase {

    private static int uncommentedMethods = 0;
    private JavaCodeAnalyzerPlugin plugin = new JavaCodeAnalyzerPlugin();

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
//        System.out.println("analyzeFile");
//        String fileContent = "";
//        BufferedReader br = new BufferedReader(new FileReader("/home/david/codesearch/src/indexer/src/main/java/org/codesearch/indexer/tasks/IndexingTask.java"));
//
//        while (br.ready()) {
//            fileContent += br.readLine() + "\n";
//        }
////        System.out.println(fileContent);
//        RepositoryDto repo = new RepositoryDto("test", "test", "test", "test", true, "SVN", null, null);
//        new JavaCodeAnalyzerPlugin().analyzeFile(fileContent, repo);
    }

    public void testParseExternalLinks() throws Exception {
//        String fileContent = "";
//        BufferedReader br = new BufferedReader(new FileReader("/home/david/workspace/svnsearch/WakMusic/src/java/servlets/AddEvent.java"));
//        while (br.ready()) {
//            fileContent += br.readLine() + "\n";
//        }
//        RepositoryDto repo = new RepositoryDto("test", "test", "test", "test", true, "SVN", null, null);
//        plugin = new JavaCodeAnalyzerPlugin();
//        plugin.analyzeFile(fileContent, repo);
//        List<ExternalLink> externalLinks = plugin.getExternalLinks();
//        List<String> imports = plugin.getImports();
//        List<ExternalUsage> usages = plugin.parseExternalLinks(fileContent, imports, externalLinks, "svn_local");
    }

    public void testCodeCommentCompletion() {
//        try {
//            try {
//                checkContentOfFile(new File("/home/david/workspace/svnsearch/"));
//            } catch (CodeAnalyzerPluginException ex) {
//                Logger.getLogger(JavaCodeAnalyzerPluginTest.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(JavaCodeAnalyzerPluginTest.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(JavaCodeAnalyzerPluginTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public void testIntegrationOfUsages() throws Exception {
        String fileContent = "";
        BufferedReader br = new BufferedReader(new FileReader("/home/david/workspace/svnsearch/WakMusic/src/java/servlets/AddEvent.java"));
        while (br.ready()) {
            fileContent += br.readLine() + "\n";
        }
        RepositoryDto repo = new RepositoryDto("test", "test", "test", "test", true, "SVN", null, null);
        plugin = new JavaCodeAnalyzerPlugin();
        plugin.analyzeFile(fileContent, repo);
        List<ExternalLink> externalLinks = plugin.getExternalLinks();
        List<String> imports = plugin.getImports();
        //  List<ExternalUsage> usages = plugin.parseExternalLinks(fileContent, imports, externalLinks, "svn_local");
        String resultString = "";
        String hlEscapeStartToken = "";
        String hlEscapeEndToken = "";
        String[] contentLines = fileContent.split("\n");
        List<Usage> usages = plugin.getUsages();
        int usageIndex = 0;
        outer:
        for (int lineNumber = 1; lineNumber < contentLines.length; lineNumber++) {
            String currentLine = contentLines[lineNumber-1];
            while (usageIndex < usages.size()) {
                Usage currentUsage = usages.get(usageIndex);
                if (currentUsage.getStartLine() == lineNumber) {
                    int startColumn = currentUsage.getStartColumn();
                    int referenceLine = currentUsage.getReferenceLine();
                    String preamble = currentLine.substring(0, startColumn - 1);//-1
                    String anchorBegin = hlEscapeStartToken + "<a class='testLink' onclick='goToLine(" + referenceLine + ");'>" + hlEscapeEndToken;
                    String anchorEnd = hlEscapeStartToken + "</a>" + hlEscapeEndToken;
                    String remainingLine = currentLine.substring(startColumn - 1 + currentUsage.getReplacedString().length());
                    currentLine = preamble + anchorBegin + currentUsage.getReplacedString() + anchorEnd + remainingLine;
                    usageIndex++;
                } else {
                    resultString += currentLine + "\n";
                    continue outer;
                }
            }
        }
        System.out.println(resultString);
    }

    private void checkContentOfFile(File file) throws FileNotFoundException, IOException, CodeAnalyzerPluginException {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                checkContentOfFile(child);
            }
        } else {
            if (file.getAbsolutePath().endsWith(".java")) {
                String fileContent = "";
                System.out.println(file.getAbsolutePath());
                BufferedReader br = new BufferedReader(new FileReader(file));
                while (br.ready()) {
                    fileContent += br.readLine() + "\n";
                }

                plugin.analyzeFile(fileContent, null);

            }
        }

    }

    class CompletionVisitor extends VoidVisitorAdapter {

        @Override
        public void visit(MethodDeclaration n, Object arg) {
            super.visit(n, arg);
            if (n.getEndLine() - n.getBeginLine() < 3) {
                return;
            }
            if (n.getJavaDoc() == null) {
                uncommentedMethods++;
                System.out.println("Method " + n.getName() + " at line " + n.getBeginLine() + " in file " + (String) arg + " is not commented #" + uncommentedMethods);
            }
        }
    }
}
