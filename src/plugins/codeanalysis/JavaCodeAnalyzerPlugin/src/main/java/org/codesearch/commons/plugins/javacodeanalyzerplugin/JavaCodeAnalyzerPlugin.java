/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.javacodeanalyzerplugin;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPlugin;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPluginException;
import org.codesearch.commons.plugins.codeanalyzing.ast.CompoundNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.Usage;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.FileNode;

/**
 *
 * @author David Froehlich
 */
public class JavaCodeAnalyzerPlugin implements CodeAnalyzerPlugin {

    private FileNode fileNode = new FileNode();
    private Map<Integer, Usage> usages = new HashMap<Integer, Usage>();
    private Map<Integer, CompoundNode> ast = new TreeMap<Integer, CompoundNode>();

    @Override
    public Map getAstForCurrentFile() throws CodeAnalyzerPluginException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void analyzeFile(String fileContent, RepositoryDto repository) throws CodeAnalyzerPluginException {
        CompilationUnit cu = null;
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(fileContent.getBytes()); //TODO maybe change specification of analyzeFile to take an inputStream as parameter
            cu = JavaParser.parse(bais);
            Visitor v = new Visitor(fileNode, usages);
            cu.accept(v, null);
            fileNode.addCompoundNodesToMap(ast);
        } catch (ParseException ex) {
            System.out.println(ex.toString());
            //TODO throw exception
        } finally {
            try {
                bais.close();
            } catch (IOException ex) {
            }
        }
        for(CompoundNode node : ast.values()){
            System.out.println(node.getOutlineLink());
        }
    }

    public String getPurposes() {
        return "java"; //FIXME
    }

    public String getVersion() {
        return "0.1-SNAPSHOT"; //
    }
}
