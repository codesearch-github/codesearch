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
 *
 * @author David Froehlich
 */
public class Usage implements Comparable<Usage>, Serializable {
    protected int length;
    protected int startColumn;
    protected int startLine;
    protected int referenceLine;
    protected String replacedString;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getReferenceLine() {
        return referenceLine;
    }

    public void setReferenceLine(int referenceLine) {
        this.referenceLine = referenceLine;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getStartColumn() {
        return startColumn;
    }

    public void setStartColumn(int startColumn) {
        this.startColumn = startColumn;
    }

    public String getReplacedString() {
        return replacedString;
    }

    public void setReplacedString(String replacedString) {
        this.replacedString = replacedString;
    }

    public Usage(int startColumn, int startLine, int length, int referenceLine, String replacedString) {
        this.length = length;
        this.startColumn = startColumn;
        this.startLine = startLine;
        this.referenceLine = referenceLine;
        this.replacedString = replacedString;
    }

    @Override
    public int compareTo(Usage other){
        if(this.startLine == other.startLine){
            return this.startColumn - other.startColumn;
        }
        return startLine - other.startLine;
    }
}
