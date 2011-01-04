/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.codeanalyzing.ast;

import java.util.List;

/**
 *
 * @author David Froehlich
 */
public class ExternalMethodLink extends ExternalLink {
    private String methodName;
    private List<String> parameterTypes;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<String> getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(List<String> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public ExternalMethodLink(int lineNumber, int column, int length, String className, String methodName, List<String> parameterTypes) {
        super(lineNumber, column, length, className);
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
    }
}
