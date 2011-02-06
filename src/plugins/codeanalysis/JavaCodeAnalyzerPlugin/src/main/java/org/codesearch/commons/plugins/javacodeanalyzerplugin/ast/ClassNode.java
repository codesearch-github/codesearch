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
 * ASTNode that represents a class in the source code
 * @author David Froehlich
 */
public class ClassNode extends CompoundNode {

    private List<MethodNode> methods = new LinkedList<MethodNode>();
    private List<VariableNode> attributes = new LinkedList<VariableNode>();
    private List<EnumNode> enums = new LinkedList<EnumNode>();

    public List<EnumNode> getEnums() {
        return enums;
    }

    public void setEnums(List<EnumNode> enums) {
        this.enums = enums;
    }

    public List<VariableNode> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<VariableNode> attributes) {
        this.attributes = attributes;
    }

    public List<MethodNode> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodNode> methods) {
        this.methods = methods;
    }

    /** {@inheritDoc} */
    @Override
    public String getOutlineName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public List<AstNode> getChildNodes() {
        List<AstNode> childNodes = new LinkedList<AstNode>();
        childNodes.addAll(attributes);
        childNodes.addAll(methods);
        childNodes.addAll(enums);
        return childNodes;
    }

    /** {@inheritDoc} */
    @Override
    public boolean showInOutline() {
        return true;
    }

    @Override
    public String getModifiers() {
        try {
            return "class " + this.getVisibility().toString();
        } catch (NullPointerException ex) {
            return ""; //FIXME
        }
    }
}
