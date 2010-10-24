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
public class ClassNode {
    private List<MethodNode> methodDeclarations;

    public List<MethodNode> getMethodDeclarations() {
        return methodDeclarations;
    }

    public void setMethodDeclarations(List<MethodNode> methodDeclarations) {
        this.methodDeclarations = methodDeclarations;
    }
}
