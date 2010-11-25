/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.codeanalyzing.ast;

import java.util.Map;

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
    public abstract void addCompoundNodesToMap(Map<Integer, CompoundNode> map);
}
