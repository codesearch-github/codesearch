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
package org.codesearch.commons.plugins.javacodeanalyzerplugin;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import org.codesearch.commons.database.DBAccess;
import org.codesearch.commons.database.DatabaseAccessException;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPluginException;
import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.ExternalUsage;
import org.codesearch.commons.plugins.codeanalyzing.ast.Usage;
import org.junit.Test;

/**
 *
 * @author David Froehlich
 */
public class JavaCodeAnalyzerPluginTest {

    private static int uncommentedMethods = 0;
    private JavaCodeAnalyzerPlugin plugin = new JavaCodeAnalyzerPlugin();

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
    @Test
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

    @Test
    public void testTEST() throws DatabaseAccessException{ //FIXME remove
        for(Entry<String, AstNode> currEntry : DBAccess.getFilesImportingTargetFile("database.DBAccess", "svn-local").entrySet()){
            System.out.println(currEntry.getKey());
        }

    }

    @Test
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
    public void iterateChildNodes(AstNode node) {
        for (AstNode childNode : node.getChildNodes()) {
            iterateChildNodes(childNode);
        }
        System.out.println(node.getModifiers());
    }

    @Test
    public void testIntegrationOfUsages() throws Exception {
        String fileContent = "";
        BufferedReader br = new BufferedReader(new FileReader("/home/david/workspace/svnsearch/codesearch/src/searcher/src/main/java/org/codesearch/searcher/server/DocumentSearcher.java"));
        while (br.ready()) {
            fileContent += br.readLine() + "\n";
        }
        plugin = new JavaCodeAnalyzerPlugin();
        plugin.analyzeFile(fileContent);
//        iterateChildNodes(plugin.getAst());
        String resultString = "";
        String hlEscapeStartToken = "";
        List<Usage> usages = plugin.getUsages();
        String hlEscapeEndToken = "";
        String[] contentLines = fileContent.split("\n");
        int usageIndex = 0;
        outer:
        for (int lineNumber = 1; lineNumber <= contentLines.length; lineNumber++) {
            String currentLine = contentLines[lineNumber - 1];
            while (usageIndex < usages.size()) {
                Usage currentUsage = usages.get(usageIndex);
                if (currentUsage.getStartLine() == lineNumber) {
                    int startColumn = currentUsage.getStartColumn();
                    String preamble = currentLine.substring(0, startColumn - 1);
                    String javaScriptEvent = "";
                    if (currentUsage instanceof ExternalUsage) {
                        javaScriptEvent = "goToUsage(" + usageIndex + ");";
                    } else {
                        int referenceLine = currentUsage.getReferenceLine();
                        javaScriptEvent = "goToLine(" + (referenceLine + 1) + ");";
                    }
                    String anchorBegin = hlEscapeStartToken + "<a class='testLink' onclick='" + javaScriptEvent + "'>" + hlEscapeEndToken;
                    String anchorEnd = hlEscapeStartToken + "</a>" + hlEscapeEndToken;
                    String remainingLine = currentLine.substring(startColumn - 1 + currentUsage.getReplacedString().length());
                    currentLine = preamble + anchorBegin + currentUsage.getReplacedString() + anchorEnd + remainingLine;

                    usageIndex++;
                } else {
                    resultString += currentLine + "\n";
                    continue outer;
                }
            }
            resultString += currentLine + "\n";
        }
        resultString = resultString.substring(0, resultString.length() - 1);
        System.out.println(resultString);
//        for (Usage currentUsage : usages) {
//            if (currentUsage instanceof ExternalUsage) {
//                ExternalUsage extUsage = (ExternalUsage) currentUsage;
//                extUsage.resolveLink("/home/david/workspace/svnsearch/codesearch/src/indexer/src/main/java/org/codesearch/indexer/tasks/IndexingTask.java", "svn_local");
//                System.out.println(extUsage.getStartLine() + ": " + extUsage.getReplacedString() + " -> " + extUsage.getTargetFilePath());
//            }
//        }
        //   System.out.println(resultString);
    }
//
//    public void testCodeCommentCompletion() throws DatabaseAccessException {
//        try {
//            try {
//                try {
//                    checkContentOfFile(new File("/home/david/codesearch/src"));
//                } catch (ParseException ex) {
//                }
//            } catch (CodeAnalyzerPluginException ex) {
//            }
//        } catch (FileNotFoundException ex) {
//        } catch (IOException ex) {
//        }
//    }

//            for (File child : file.listFiles()) {
//                checkContentOfFile(child);
//            }
//        } else {
//            String truncFileName = file.getAbsolutePath().replaceAll("/home/david/codesearch/", "");
//            if (file.getAbsolutePath().endsWith(".java") && !file.getAbsolutePath().contains("searcher") && !file.getAbsolutePath().contains("/test/") && !file.getAbsolutePath().contains("jhighlight") && !file.getAbsolutePath().contains("resources")) {
//                CompletionVisitor cv = new CompletionVisitor();
//                CompilationUnit cu = JavaParser.parse(file);
//                cu.accept(cv, truncFileName);
//            }
//        }

    @Test
    private void checkContentOfFile(File file) throws FileNotFoundException, IOException, CodeAnalyzerPluginException, ParseException, DatabaseAccessException {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                checkContentOfFile(child);
            }
        } else {
            if (file.getAbsolutePath().endsWith(".java") && !file.getAbsolutePath().contains("jhighlight") && !file.getAbsolutePath().contains("searcher") && !file.getAbsolutePath().contains("Test")) {
//                System.out.println(file.getAbsolutePath());
//                String fileContent = "";
//                BufferedReader br = new BufferedReader(new FileReader(file));
//                while (br.ready()) {
//                    fileContent += br.readLine() + "\n";
//                }
//                fileContent = fileContent.substring(0, fileContent.length() - 1);
//
//                plugin.analyzeFile(fileContent);
//                for (Usage u : plugin.getUsages()) {
//                    if (u instanceof ExternalUsage) {
//                        ((ExternalUsage) u).resolveLink(file.getAbsolutePath(), "svn_local");
//                    }
//                }
                String fileName = file.getAbsolutePath();
                CompilationUnit cu = JavaParser.parse(file);
                cu.accept(new CompletionVisitor(), fileName);
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
