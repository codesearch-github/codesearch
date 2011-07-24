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

package org.codesearch.commons.plugins.codeanalyzing.ast;

import java.io.Serializable;

/**
 * represents the usage of a code element (variable, classe, enum, etc.) in the source code
 * 
 * @author David Froehlich
 */
public class Usage implements Comparable<Usage>, Serializable {
    /** the length of the usage in characters */
    protected int length;
    /** the column at which the usage starts in the original file */
    protected int startColumn;
    /** the line number of the usage in the original file */
    protected int startLine;
    /** the line number of the element that is referenced */
    protected int referenceLine;
    /** the string that in the additional file that will be replaced by the usage link
     usually the name of the element */
    protected String replacedString;

    public Usage(){
        
    }

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

    /**
     * returns 0 if both usages have the exact same position (line and column)
     * can be used to sort ascending by line number and then descending by column
     * @param other the usage to compare this one with
     * @return 
     */
    @Override
    public int compareTo(Usage other){
        if(this.startLine == other.startLine){
            return other.startColumn - this.startColumn;
        }
        return startLine - other.startLine;
    }
}
