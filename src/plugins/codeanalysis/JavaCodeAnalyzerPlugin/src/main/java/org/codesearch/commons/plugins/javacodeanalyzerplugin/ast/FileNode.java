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
 * AST element that represents a class in the source code file
 * @author David Froehlich
 */
public class FileNode extends CompoundNode {
    private List<ClassNode> classes = new LinkedList<ClassNode>();
    private List<EnumNode> enums = new LinkedList<EnumNode>();

    public List<EnumNode> getEnums() {
        return enums;
    }

    public void setEnums(List<EnumNode> enums) {
        this.enums = enums;
    }

    public List<ClassNode> getClasses() {
        return classes;
    }

    public void setClasses(List<ClassNode> classes) {
        this.classes = classes;
    }

    /** {@inheritDoc} */
    @Override
    public String getOutlineName() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<AstNode> getChildNodes() {
        LinkedList<AstNode> childs = new LinkedList<AstNode>();
        childs.addAll(classes);
        childs.addAll(enums);
        return childs;
    }

    /** {@inheritDoc} */
    @Override
    public boolean showInOutline() {
        return false;
    }

    @Override
    public String getModifiers() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof FileNode){
            return super.equals(obj);
        }
        return false;
    }


}
