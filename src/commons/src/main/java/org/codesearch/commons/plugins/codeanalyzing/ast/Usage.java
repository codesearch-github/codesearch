/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.codeanalyzing.ast;

/**
 *
 * @author David Froehlich
 */
public class Usage {
    private int length;
    private int referencePosition;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getReferencePosition() {
        return referencePosition;
    }

    public void setReferencePosition(int referencePosition) {
        this.referencePosition = referencePosition;
    }

    public Usage(int length, int referencePosition) {
        this.length = length;
        this.referencePosition = referencePosition;
    }
}
