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

import japa.parser.ParseException;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
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

    private static int count = 0;
    private static int uncommentedMethods = 0;
    private JavaCodeAnalyzerPlugin plugin = new JavaCodeAnalyzerPlugin();
     /* Logger */
    private static final Logger LOG = Logger.getLogger(JavaCodeAnalyzerPluginTest.class);

//    /**
//     * Test of getAstForCurrentFile method, of class JavaCodeAnalyzerPlugin.
//     */
//    public void testGetAstForCurrentFile() throws Exception {
//        LOG.info("getAstForCurrentFile");
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
        LOG.info("analyzeFile");
        String fileContent = "";
        BufferedReader br = new BufferedReader(new FileReader("/home/david/workspace/codesearch/src/commons/src/main/java/org/codesearch/commons/plugins/codeanalyzing/ast/Visibility.java"));

        while (br.ready()) {
            fileContent += br.readLine() + "\n";
        }
//        LOG.info(fileContent);
        plugin.analyzeFile(fileContent);
        soutChildNodes(plugin.getAst());
    }

    private void soutChildNodes(AstNode ast){
        for(AstNode currentChild : ast.getChildNodes()){
            LOG.info(currentChild.getName() + "\n");
            soutChildNodes(currentChild);
        }
    }

    @Test
    public void testTEST() throws DatabaseAccessException { //FIXME remove
        for (Entry<String, AstNode> currEntry : DBAccess.getFilesImportingTargetFile("database.DBAccess", "svn-local").entrySet()) {
            LOG.info(currEntry.getKey());
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
        LOG.info(node.getModifiers());
    }

    @Test
    public void testIntegrationOfUsages() throws Exception {
        doStuff();
    }

    private void doStuff() throws CodeAnalyzerPluginException, DatabaseAccessException, FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader("/home/david/workspace/svnsearch/svncommons/SvnSearchBean.java"));
        String fileContent = "";
        while(br.ready()){
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
                if(usageIndex == 566){
                    getClass();
                }
                if (currentUsage.getStartLine() == lineNumber) {
                    int startColumn = currentUsage.getStartColumn();

                    String preamble = null;
                    try {
                        preamble = currentLine.substring(0, startColumn - 1);
                    } catch (StringIndexOutOfBoundsException ex) {
                        throw new DatabaseAccessException("asdf");
                    }

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
        LOG.info(resultString);
    }

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
    public void checkContentOfFile(File file) throws FileNotFoundException, IOException, CodeAnalyzerPluginException, ParseException, DatabaseAccessException, Exception {
//        if (file.isDirectory()) {
//            for (File child : file.listFiles()) {
//                checkContentOfFile(child);
//            }
//        } else {
//            if (file.getAbsolutePath().endsWith(".java") && !file.getAbsolutePath().contains("jhighlight") && !file.getAbsolutePath().contains("generated")) {
//                String fileContent = "";
//                BufferedReader br = new BufferedReader(new FileReader(file));
//                while (br.ready()) {
//                    fileContent += br.readLine() + "\n";
//                }
//                if (file.getAbsolutePath().equals("/home/david/workspace/codesearch/src/searcher/target/.generated/com/google/gwt/user/cellview/client/CellTable_TemplateImpl.java")) {
//                    getClass();
//                }
//                try {
//                    doStuff(fileContent);
//                } catch (DatabaseAccessException ex) {
//                    getClass();
//                }
//            }
//        }
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
                LOG.info("Method " + n.getName() + " at line " + n.getBeginLine() + " in file " + (String) arg + " is not commented #" + uncommentedMethods);
            }
        }
    }
}
