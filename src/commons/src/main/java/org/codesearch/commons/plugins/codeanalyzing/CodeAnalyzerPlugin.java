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

package org.codesearch.commons.plugins.codeanalyzing;

import java.util.List;
import org.codesearch.commons.plugins.Plugin;
import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.Usage;

/**
 * A plugin used to analyze the content of a source code file
 * Parses all information necessary to display an outline of the file and provide code navigation
 * @author David Froehlich
 */
public interface CodeAnalyzerPlugin extends Plugin {

    /**
     * returns all a list of all type declarations created in the file
     * @return the type declarations
     * @throws CodeAnalyzerPluginException if the analyzeFile method was not executed before
     */
    List<String> getTypeDeclarations() throws CodeAnalyzerPluginException;

    /**
     * returns a list of all usages in the file
     * @return the usages
     * @throws CodeAnalyzerPluginExceptionv if the analyzeFile method was not executed before
     */
    List<Usage> getUsages() throws CodeAnalyzerPluginException;

    /**
     * analyzes the file and extracts information about the type declarations, usages, external links and the ast
     * @return the AstNode of the file
     * @param fileContent the content of the file
     * @throws CodeAnalyzerPluginException if the analysis failed
     */
     void analyzeFile(String fileContent) throws CodeAnalyzerPluginException;

     AstNode getAst();

    /**
     * returns all imports declared in the file
     * @return the imports
     * @throws CodeAnalyzerPluginException if the analyzeFile method was not executed before
     */
    List<String> getImports() throws CodeAnalyzerPluginException;
}
