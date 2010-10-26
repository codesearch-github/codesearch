/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.codeanalyzing.ast;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author David Froehlich
 */
public class ClassNode extends ASTElement{
    private List<MethodNode> methodDeclarations = new LinkedList<MethodNode>();

    public List<MethodNode> getMethodDeclarations() {
        return methodDeclarations;
    }

    public void setMethodDeclarations(List<MethodNode> methodDeclarations) {
        this.methodDeclarations = methodDeclarations;
    }

    @Override
    public String getOutlineForChildElements() {
        String outlineString = "  Class definition: "+ this.getName() + " from: "+this.getDeclarationPosition() + ", length: "+this.getNodeLength()+"\n";
        for(MethodNode method : methodDeclarations){
            outlineString += method.getOutlineForChildElements();
        }
        return outlineString;
    }
}
