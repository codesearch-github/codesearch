/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.codeanalyzing;

import java.util.Map;
import java.util.Set;

import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.plugins.Plugin;
import org.codesearch.commons.plugins.codeanalyzing.ast.FileNode;

/**
 *
 * @author David Froehlich
 */
public interface CodeAnalyzerPlugin extends Plugin {
    Map<String, FileNode> getAstForRepository(Set<String> filenames, RepositoryDto repository);
}
