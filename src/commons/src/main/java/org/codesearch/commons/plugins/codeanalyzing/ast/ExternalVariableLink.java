/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.codeanalyzing.ast;

/**
 *
 * @author David Froehlich
 */
public class ExternalVariableLink extends ExternalLink {
    private String variableName;

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public ExternalVariableLink(int lineNumber, int column, int length, String className, String variableName) {
        super(lineNumber, column, length, className);
        this.variableName = variableName;
    }
}
