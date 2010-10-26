/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.codeanalyzing.ast;

/**
 *
 * @author David Froehlich
 */
public abstract class ASTElement {
    protected int declarationPosition;
    protected int nodeLength;
    private String name;

    public int getNodeLength() {
        return nodeLength;
    }

    public void setNodeLength(int nodeLength) {
        this.nodeLength = nodeLength;
    }

    public int getDeclarationPosition() {
        return declarationPosition;
    }

    public void setDeclarationPosition(int declarationPosition) {
        this.declarationPosition = declarationPosition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract String getOutlineForChildElements();
}
