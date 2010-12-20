/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.codeanalyzing.ast;

import java.io.Serializable;

/**
 * Superclass for all ASTNodes used for code analysis.
 * @author David Froehlich
 */
public abstract class Node implements Serializable, Comparable<Node>{
    protected int startLine;
    protected int startPositionInLine;
    protected int startPositionAbsolute;
    protected String name;

    @Override
    public int compareTo(Node other){
        if(this.startLine == other.startLine){
            return this.startPositionInLine - other.startPositionInLine;
        }
        return startLine - other.startLine;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getStartPositionInLine() {
        return startPositionInLine;
    }

    public void setStartPositionInLine(int startPositionInLine) {
        this.startPositionInLine = startPositionInLine;
    }

    public int getStartPositionAbsolute() {
        return startPositionAbsolute;
    }

    public void setStartPositionAbsolute(int startPositionAbsolute) {
        this.startPositionAbsolute = startPositionAbsolute;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
