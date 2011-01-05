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
import org.codesearch.commons.plugins.codeanalyzing.ast.Visibility;

/**
 * ASTNode that represents a class in the source code
 * @author David Froehlich
 */
public class ClassNode extends CompoundNode {

    private List<MethodNode> methods = new LinkedList<MethodNode>();
    private List<VariableNode> attributes = new LinkedList<VariableNode>();

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

    public String getOutlineForChildElements() {
        //String outlineString = "  Class definition: "+ this.getName() + " from: "+this.getStartPositionInLin() + ", length: "+this.getNodeLength()+"\n";

        //TODo add useful outlineName for class
        String outlineString = "";
        for (MethodNode method : methods) {
            outlineString += method.getOutlineForChildElements();
        }
        return outlineString;
    }

    @Override
    public String getOutlineName() {
        return name;
    }

    @Override
    public void addCompoundNodesToList(List<AstNode> nodes) {
        for (MethodNode method : methods) {
            nodes.add(method);
            method.addCompoundNodesToList(nodes);
        }
        for (VariableNode var : attributes){
            if(var.getVisibility() == Visibility.PUBLIC || var.getVisibility() == Visibility.PROTECTED){
                nodes.add(var);
            }
        }
    }

    @Override
    public List<AstNode> getChildNodes() {
        List<AstNode> childNodes = new LinkedList<AstNode>();
        childNodes.addAll(this.attributes);
        for(MethodNode currentMethod : methods){
            childNodes.add(currentMethod);
            childNodes.addAll(currentMethod.getChildNodes());
        }
        return childNodes;
    }
}
