/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.javacodeanalyzerplugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codesearch.commons.plugins.PluginLoaderException;
import org.codesearch.commons.plugins.codeanalyzing.ast.FileNode;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.constants.MimeTypeNames;
import org.codesearch.commons.plugins.PluginLoader;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPlugin;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPluginException;
import org.codesearch.commons.plugins.codeanalyzing.ast.MethodNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.Usage;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;
import org.codesearch.commons.plugins.vcs.VersionControlPluginException;
import org.codesearch.javacodeanalyzerplugin.astanalyzer.JavaAstVisitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

/**
 *
 * @author David Froehlich
 */
public class JavaCodeAnalyzerPlugin implements CodeAnalyzerPlugin {

    private PluginLoader pluginLoader = new PluginLoader();
    private VersionControlPlugin versionControlPlugin;
    private String fileContent;
    private FileNode fileNode;
    private Map<Integer, Usage> usages = new HashMap<Integer, Usage>();
    
    //TODO add spring...
    @Override
    public void analyzeFile(String fileName, RepositoryDto repository) throws CodeAnalyzerPluginException {
        try {
            fileNode = new FileNode();
            versionControlPlugin = pluginLoader.getPlugin(VersionControlPlugin.class, repository.getVersionControlSystem());
            fileContent = versionControlPlugin.getFileContentForFilePath(fileName).toString();
            ASTParser parser = ASTParser.newParser(AST.JLS3);
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            parser.setResolveBindings(false);
            parser.setSource(fileContent.toCharArray());
            ASTNode root = parser.createAST(null);
            JavaAstVisitor visitor = new JavaAstVisitor(fileNode, usages);
            root.accept(visitor);
        } catch (VersionControlPluginException ex) {
            throw new CodeAnalyzerPluginException("Could not retrieve file content from VersionControlPlugin\n" + ex);
        } catch (PluginLoaderException ex) {
            throw new CodeAnalyzerPluginException("No plugin for set version control type found");
        }

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
