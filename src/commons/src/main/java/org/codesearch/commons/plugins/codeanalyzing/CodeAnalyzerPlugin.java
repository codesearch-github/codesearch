/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.codeanalyzing;


import java.util.Map;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.plugins.Plugin;
import org.codesearch.commons.plugins.codeanalyzing.ast.CompoundNode;

/**
 *
 * @author David Froehlich
 */
public interface CodeAnalyzerPlugin extends Plugin {
    Map<Integer, CompoundNode> getAstForCurrentFile() throws CodeAnalyzerPluginException;
    void analyzeFile(String fileContent, RepositoryDto repository) throws CodeAnalyzerPluginException;
}
