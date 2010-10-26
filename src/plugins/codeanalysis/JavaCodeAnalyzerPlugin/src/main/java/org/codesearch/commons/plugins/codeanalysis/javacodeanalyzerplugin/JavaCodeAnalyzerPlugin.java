/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.codeanalysis.javacodeanalyzerplugin;

import java.util.HashMap;
import java.util.Map;
import org.codesearch.commons.plugins.PluginLoaderException;
import org.codesearch.commons.plugins.codeanalyzing.ast.FileNode;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.constants.MimeTypeNames;
import org.codesearch.commons.plugins.PluginLoader;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPlugin;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPluginException;
import org.codesearch.commons.plugins.codeanalyzing.ast.Usage;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;
import org.codesearch.commons.plugins.vcs.VersionControlPluginException;
import org.codesearch.commons.plugins.codeanalysis.javacodeanalyzerplugin.astanalyzer.JavaAstVisitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.internal.core.jdom.CompilationUnit;
import org.springframework.stereotype.Component;

/**
 *
 * @author David Froehlich
 */
@Component
public class JavaCodeAnalyzerPlugin implements CodeAnalyzerPlugin {
    private FileNode fileNode;
    private Map<Integer, Usage> usages = new HashMap<Integer, Usage>();

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
    }

    @Override
    public FileNode getAstForCurrentFile() {
        return fileNode;
    }

    @Override
    public String getPurposes() {
        return MimeTypeNames.JAVA;
    }

    @Override
    public String getVersion() {
        return "0.1-SNAPSHOT";
    }
}
