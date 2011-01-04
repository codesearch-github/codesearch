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
 * AST element that represents a class in the source code file
 * @author David Froehlich
 */
public class FileNode extends CompoundNode {
    private List<ClassNode> classes = new LinkedList<ClassNode>();

    public List<ClassNode> getClasses() {
        return classes;
    }

    public void setClasses(List<ClassNode> classes) {
        this.classes = classes;
    }
    
    public String getOutlineForChildElements(){
        String outlineString = "File: "+this.getName()+" from: "+this.getStartPositionInLine() + ", length: "+this.getNodeLength()+"\n";
        for(ClassNode currentClass : classes){
            outlineString += currentClass.getOutlineForChildElements();
        }
        return outlineString;
    }

    @Override
    public String getOutlineLink() {
        return "";
    }

    @Override
    public void addCompoundNodesToList(List<AstNode> nodes) {
        for(ClassNode clazz : classes){
            nodes.add(clazz);
            clazz.addCompoundNodesToList(nodes);
        }
    }
}
