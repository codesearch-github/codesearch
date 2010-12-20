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
public abstract class CompoundNode extends Node {
    protected int nodeLength;

    public int getNodeLength() {
        return nodeLength;
    }

    public void setNodeLength(int nodeLength) {
        this.nodeLength = nodeLength;
    }

    public abstract String getOutlineLink();

    /**
     * recursively adds all compound nodes within the current node to the list
     * @param nodes
     */
    public abstract void addCompoundNodesToList(List<CompoundNode> nodes);
}
