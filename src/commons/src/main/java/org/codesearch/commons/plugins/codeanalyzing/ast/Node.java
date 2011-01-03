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
