/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.codeanalyzing;


import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.plugins.Plugin;
import org.codesearch.commons.plugins.codeanalyzing.ast.FileNode;

/**
 *
 * @author David Froehlich
 */
public interface CodeAnalyzerPlugin extends Plugin {
    FileNode getAstForCurrentFile() throws CodeAnalyzerPluginException;
    void analyzeFile(String File, RepositoryDto repository) throws CodeAnalyzerPluginException;
}
