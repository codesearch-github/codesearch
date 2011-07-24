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
import org.codesearch.commons.plugins.codeanalyzing.ast.Visibility;

/**
 * ASTElement that represents a variable in the source code
 * @author David Froehlich
 */
public class VariableNode extends AstNode {

    private String type;
    private boolean attribute;

    public VariableNode() {
        this.visibility = Visibility.NONE;
    }

    public boolean getAttribute() {
        return attribute;
    }

    public void setAttribute(boolean attribute) {
        this.attribute = attribute;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /** {@inheritDoc} */
    @Override
    public String getOutlineName() {
        if (attribute) {
            return type + " " + name;
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<AstNode> getChildNodes() {
        return new LinkedList<AstNode>();
    }

    /** {@inheritDoc} */
    @Override
    public boolean showInOutline() {
//        if(visibility != Visibility.NONE) {
//            return true;
//        } else {
//            return false;
//        }
        return attribute;
    }

    @Override
    public String getModifiers() {
        return "variable " + visibility.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VariableNode) {
            VariableNode other = (VariableNode) obj;
            return super.equals(obj) && (other.attribute == this.attribute) && (other.type.equals(this.type));
        }
        return false;
    }
}
