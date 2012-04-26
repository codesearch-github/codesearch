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
package org.codesearch.commons.plugins.javacodeanalyzerplugin;

import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.log4j.Logger;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPlugin;
import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
import org.codesearch.commons.plugins.codeanalyzing.xml.XmlCodeAnalyzerPlugin;
import org.codesearch.commons.plugins.codeanalyzing.xml.ast.XmlNode;
import org.junit.Test;

/**
 *
 * @author Samuel Kogler
 */
public class XmlCodeAnalyzerPluginTest {

    /* Logger */
    private static final Logger LOG = Logger.getLogger(XmlCodeAnalyzerPluginTest.class);

    @Test
    public void testAST() throws Exception {
        LOG.info("analyzeFile");
        String fileContent = "";
        BufferedReader br = new BufferedReader(new FileReader("/home/daasdingo/workspace/svnsearch/src_java/main/webapp/details.xhtml"));
        while (br.ready()) {
            fileContent += br.readLine() + "\n";
        }

        CodeAnalyzerPlugin plugin = new XmlCodeAnalyzerPlugin();
        plugin.analyzeFile(fileContent);
        AstNode ast = plugin.getAst();
        recursiveOutput((XmlNode) ast, 0);
    }

    private void recursiveOutput(XmlNode node, int level) {
        for (AstNode childNode : node.getChildNodes()) {
            recursiveOutput((XmlNode) childNode, level + 1);
        }
        String preamble = "";
        for (int i = 0; i < level; i++) {
            preamble += "  ";
        }
        LOG.info(preamble + node.getOutlineName());
    }
}
