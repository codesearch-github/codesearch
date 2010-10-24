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
import org.codesearch.commons.plugins.codeanalyzing.ast.MethodNode;
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
    //TODO add spring...

    @Override
    public Map<String, FileNode> getAstForRepository(Set<String> filenames, RepositoryDto repository) {
        try {
            versionControlPlugin = pluginLoader.getPlugin(VersionControlPlugin.class, repository.getVersionControlSystem());
        } catch (PluginLoaderException ex) {
            //TODO add exception handling
        }
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        Map<String, FileNode> asts = new HashMap<String, FileNode>();

        String fileContent = "";
        for (String filename : filenames) {
            try {
                fileContent = versionControlPlugin.getFileContentForFilePath(filename).toString();
            } catch (VersionControlPluginException ex) {
                //TODO add exception handling
            }
            parser.setResolveBindings(true);
            parser.setSource(fileContent.toCharArray());
            ASTNode root = parser.createAST(null);
            FileNode fileNode = new FileNode();
            JavaAstVisitor visitor = new JavaAstVisitor(fileNode);
            root.accept(visitor);
            asts.put(filename, fileNode);
        }
        return asts;
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
