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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

    private Visibility visibility;
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

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }
//TODO probably remove method
    public String getOutlineForChildElements() {
        String parameterString = "";
        String returnString = "";
        for (VariableNode v : parameters) {
            parameterString += v.getType() + " " + v.getName() + ",";
        }
        if (parameterString.length() > 0) {
            parameterString = parameterString.substring(0, parameterString.length() - 1);
        }
        if (returnType != null) {
            returnString = ": " + returnType;
        }
        String outlineString = "";
        if (constructor) {
            outlineString = "    Constructor declaration " + this.getName() + "(" + parameterString + ") from " + this.getStartPositionAbsolute() + ", length: " + this.getNodeLength() + "\n";
        } else {
            outlineString = "    Method declaration " + this.getName() + "(" + parameterString + ")" + returnString + " from " + super.getStartPositionAbsolute() + ", length: " + super.getNodeLength() + "\n";
        }
        return outlineString;
    }

    @Override
    public String getOutlineLink() {
        String parameterString = "";
        String returnString = "";
        String visibilityString = "";
        switch (visibility) {
            case private_vis:
                visibilityString = "private ";
                break;
            case public_vis:
                visibilityString = "public ";
                break;
            case protected_vis:
                visibilityString = "protected ";
                break;
        }
        for (VariableNode v : parameters) {
            parameterString += v.getType() + " " + v.getName() + ", ";
        }
        if (parameterString.length() > 0) {
            parameterString = parameterString.substring(0, parameterString.length() - 2);
        }
        if (returnType != null) {
            returnString = ": " + returnType;
        }
        String outlineString = "<a href='#" + startLine + "'>"  + visibilityString + this.getName() + "(" + parameterString + ")";
        if (!constructor) {
            outlineString += returnString;
        }
        outlineString += "</a>";
        return outlineString;
    }

    @Override
    public void addCompoundNodesToList(List<AstNode> nodes) {
    }
}
