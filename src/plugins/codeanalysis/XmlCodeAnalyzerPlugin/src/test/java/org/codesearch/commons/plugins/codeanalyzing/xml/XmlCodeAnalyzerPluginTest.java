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
package org.codesearch.commons.plugins.codeanalyzing.xml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPlugin;
import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
import org.junit.Test;

/**
 * Simply tests whether analyzing works and produces a non-null AST.
 * @author Samuel Kogler
 */
public class XmlCodeAnalyzerPluginTest {

    private CodeAnalyzerPlugin plugin = new XmlCodeAnalyzerPlugin();
    private String fileContent;
    
    public XmlCodeAnalyzerPluginTest() throws IOException {
        fileContent = IOUtils.toString(
                XmlCodeAnalyzerPluginTest.class.getResourceAsStream("test.xml"), "UTF-8");
    }

    @Test
    public void testAstNotEmpty() throws Exception {
        plugin.analyzeFile(fileContent);
        AstNode ast = plugin.getAst();
        assertNotNull(ast);
        assertNotNull(ast.getChildNodes());
        assertFalse(ast.getChildNodes().isEmpty());
    }
}
