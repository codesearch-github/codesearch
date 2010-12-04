/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.codeanalysis.javacodeanalyzerplugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.codesearch.commons.plugins.codeanalysis.javacodeanalyzerplugin.ast.FileNode;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.constants.MimeTypeNames;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPlugin;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPluginException;
import org.codesearch.commons.plugins.codeanalyzing.ast.Usage;
import org.codesearch.commons.plugins.codeanalysis.javacodeanalyzerplugin.astanalyzer.JavaAstVisitor;
import org.codesearch.commons.plugins.codeanalyzing.ast.CompoundNode;
import org.codesearch.org.eclipse.jdt.core.dom.AST;
import org.codesearch.org.eclipse.jdt.core.dom.ASTNode;
import org.codesearch.org.eclipse.jdt.core.dom.ASTParser;
import org.springframework.stereotype.Component;

/**
 *
 * @author David Froehlich
 */
@Component
public class JavaCodeAnalyzerPlugin implements CodeAnalyzerPlugin {

    private FileNode fileNode;
    private Map<Integer, Usage> usages = new HashMap<Integer, Usage>();
    private Map<Integer, CompoundNode> ast = new TreeMap<Integer, CompoundNode>();

    //TODO add spring...
    @Override
    public void analyzeFile(String fileContent, RepositoryDto repository) throws CodeAnalyzerPluginException {
        fileNode = new FileNode();
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
        parser.setSource(fileContent.toCharArray());
        ASTNode root = parser.createAST(null);
        JavaAstVisitor visitor = new JavaAstVisitor(fileNode, usages);
        root.accept(visitor);
        fileNode.addCompoundNodesToMap(ast);
        int chars = 0;
        String[] lines = fileContent.split("\n");
        int lineNumber = 0;
        for (Entry<Integer, CompoundNode> currentNode : ast.entrySet()) {
            while (lineNumber < lines.length) {
                if (currentNode.getKey() < chars) {
                    currentNode.getValue().setStartLine(lineNumber);
                    break;
                }
                chars += lines[lineNumber].length() + 1;
                lineNumber++;
            }
        }
        for(CompoundNode node : ast.values()){
            System.out.println(node.getOutlineLink());
        }
    }

    @Override
    public Map<Integer, CompoundNode> getAstForCurrentFile() {
        return ast;
    }

    @Override
    public String getPurposes() {
        return "java";
    }

    @Override
    public String getVersion() {
        return "0.1-SNAPSHOT";
    }
}
