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

package org.codesearch.commons.plugins.codeanalyzing.xml.ast;

import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.CompoundNode;

/**
 * An XML AST node.
 * @author Samuel Kogler
 */
public class XmlNode extends CompoundNode {

    private List<XmlNode> xmlNodes = new LinkedList<XmlNode>();

    public List<XmlNode> getXmlNodes() {
        return xmlNodes;
    }

    /** {@inheritDoc} */
    @Override
    public String getOutlineName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public boolean showInOutline() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public List<AstNode> getChildNodes() {
        List<AstNode> astNodes = new LinkedList<AstNode>();
        astNodes.addAll(xmlNodes);

        return astNodes;
    }

    @Override
    public String getModifiers() {
        return "";
    }
}
