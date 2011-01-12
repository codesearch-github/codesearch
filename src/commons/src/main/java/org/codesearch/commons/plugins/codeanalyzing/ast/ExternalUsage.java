/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.codeanalyzing.ast;

import org.codesearch.commons.database.DatabaseAccessException;

/**
 *
 * @author David Froehlich
 */
public abstract class ExternalUsage extends Usage {

    protected String targetClassName;
    protected String targetFilePath;

    public ExternalUsage() {
    }

    public ExternalUsage(int startPositionInLine, int startLine, int length, String replacedString, String targetClassName) {
        super.startColumn = startPositionInLine;
        super.startLine = startLine;
        super.length = length;
        super.replacedString = replacedString;
        this.targetClassName = targetClassName;
    }

    public abstract void resolveLink(String originFilePath, String repository) throws DatabaseAccessException;

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
