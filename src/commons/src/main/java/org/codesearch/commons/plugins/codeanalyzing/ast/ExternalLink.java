/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.codeanalyzing.ast;

/**
 *
 * @author David Froehlich
 */
public class ExternalLink {
    protected int lineNumber;
    protected int column;
    protected int length;
    protected String className;

    public ExternalLink(int lineNumber, int column, int length, String className) {
        this.lineNumber = lineNumber;
        this.column = column;
        this.length = length;
        this.className = className;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
}
