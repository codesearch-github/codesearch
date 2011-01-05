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
package org.codesearch.commons.plugins.codeanalyzing.ast;

import java.util.List;

/**
 * Superclass for all ASTNodes that have a body (for instance MethodNodes and ClassNodes)
 * @author David Froehlich
 */
public abstract class CompoundNode extends AstNode {

    /**
     * returns a list of all child nodes
     * @return all child nodes
     */
    public abstract List<AstNode> getChildNodes();

    protected int nodeLength;

    public int getNodeLength() {
        return nodeLength;
    }

    public void setNodeLength(int nodeLength) {
        this.nodeLength = nodeLength;
    }
    /**
     * recursively adds all compound nodes within the current node to the list
     * @param nodes
     */
    public abstract void addCompoundNodesToList(List<AstNode> nodes);
}
