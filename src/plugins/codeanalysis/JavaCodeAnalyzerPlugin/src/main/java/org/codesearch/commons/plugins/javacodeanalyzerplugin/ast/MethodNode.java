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
 * ASTElement that represents a method in the source code
 * @author David Froehlich
 */
public class MethodNode extends CompoundNode {
    private List<VariableNode> parameters = new LinkedList<VariableNode>();
    private String returnType;
    private boolean constructor;
    private List<VariableNode> localVariables = new LinkedList<VariableNode>();

    public List<VariableNode> getLocalVariables() {
        return localVariables;
    }

    public void setLocalVariables(List<VariableNode> localVariables) {
        this.localVariables = localVariables;
    }

    public boolean isConstructor() {
        return constructor;
    }

    public void setConstructor(boolean constructor) {
        this.constructor = constructor;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public List<VariableNode> getParameters() {
        return parameters;
    }

    public void setParameters(List<VariableNode> parameters) {
        this.parameters = parameters;
    }

    /** {@inheritDoc} */
    @Override
    public String getOutlineName() {
        String parameterString = "";
        String returnString = "";
        
        for (VariableNode v : parameters) {
            parameterString += v.getType() + " " + v.getName() + ", ";
        }
        if (parameterString.length() > 0) {
            parameterString = parameterString.substring(0, parameterString.length() - 2);
        }
        if (returnType != null) {
            returnString = ": " + returnType;
        }
        String outlineString = this.getName() + "(" + parameterString + ")";
        if (!constructor) {
            outlineString += returnString;
        }
        return outlineString;
    }

    /** {@inheritDoc} */
    @Override
    public List<AstNode> getChildNodes() {
        List<AstNode> childNodes = new LinkedList<AstNode>();
        childNodes.addAll(localVariables);
        childNodes.addAll(parameters);
        return childNodes;
    }

    /** {@inheritDoc} */
    @Override
    public boolean showInOutline() {
        return true;
    }

    @Override
    public String getModifiers() {
        return "method " + visibility.toString();
    }


}
