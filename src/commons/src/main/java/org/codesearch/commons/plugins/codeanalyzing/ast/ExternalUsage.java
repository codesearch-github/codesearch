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

import org.codesearch.commons.database.DatabaseAccessException;
import org.codesearch.commons.database.DatabaseEntryNotFoundException;

/**
 *
 * @author David Froehlich
 */
public abstract class ExternalUsage extends Usage {

    protected String targetClassName;
    protected String targetFilePath;

    public ExternalUsage() {
    }

    /**
     * creates a new instance of ExternalUsage
     * @param startPositionInLine the column where the usage starts in the line
     * @param startLine the line of the usage
     * @param length the length of the usage in chars
     * @param replacedString the string that is replaced in the line
     * @param targetClassName the name of the type that where the usage points to
     */
    public ExternalUsage(int startPositionInLine, int startLine, int length, String replacedString, String targetClassName) {
        super.startColumn = startPositionInLine;
        super.startLine = startLine;
        super.length = length;
        super.replacedString = replacedString;
        this.targetClassName = targetClassName;
    }

    /**
     * sets the targetFilePath and the reference line for the usage
     * @param originFilePath the file containing the usage
     * @param repository the repository holding the file
     * @throws DatabaseAccessException
     */
    public abstract void resolveLink(String targetFilePath, AstNode ast);

    public String getTargetClassName() {
        return targetClassName;
    }

    public String getTargetFilePath() {
        return targetFilePath;
    }

    public void setTargetFilePath(String targetFilePath){
        this.targetFilePath = targetFilePath;
    }
}
