/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.codeanalyzing.ast;

/**
 *
 * @author David Froehlich
 */
public class ExternalUsage extends Usage{
    private String targetFile;

    public ExternalUsage(int startPositionInLine, int startLine, int length, int referenceLine, String replacedString, String targetFile) {
        super(startPositionInLine, startLine, length, referenceLine, replacedString);
        this.targetFile = targetFile;
    }

    public String getTargetFile() {
        return targetFile;
    }
}
