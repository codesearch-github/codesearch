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
package org.codesearch.commons.plugins.javacodeanalyzerplugin.ast;

import java.util.LinkedList;
import java.util.List;

import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.CompoundNode;

/**
 * ASTNode that represents an enum in the source code
 * @author David Froehlich
 */
public class EnumNode extends CompoundNode {

    private List<EnumValueNode> values = new LinkedList<EnumValueNode>();

    public List<EnumValueNode> getValues() {
        return values;
    }

    public void setValues(List<EnumValueNode> values) {
        this.values = values;
    }

    public EnumNode() {
    }

    @Override
    public String getOutlineName() {
        return super.name;
    }

    @Override
    public boolean showInOutline() {
        return true;
    }

    @Override
    public List<AstNode> getChildNodes() {
        List<AstNode> childNodes = new LinkedList<AstNode>();
        childNodes.addAll(values);
        return childNodes;
    }

    @Override
    public String getModifiers() {
        return "enum " + visibility.toString().toLowerCase();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EnumNode) {
            return super.equals(obj);
        }
        return false;
    }
}
