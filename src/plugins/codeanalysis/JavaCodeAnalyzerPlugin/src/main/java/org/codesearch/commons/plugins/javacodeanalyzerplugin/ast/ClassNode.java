/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.javacodeanalyzerplugin.ast;

import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.MethodNode;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.codesearch.commons.plugins.codeanalyzing.ast.CompoundNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.Node;

/**
 * ASTNode that represents a class in the source code
 * @author David Froehlich
 */
public class ClassNode extends CompoundNode {

    private List<MethodNode> methods = new LinkedList<MethodNode>();

    public List<MethodNode> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodNode> methods) {
        this.methods = methods;
    }

    public String getOutlineForChildElements() {
        //String outlineString = "  Class definition: "+ this.getName() + " from: "+this.getStartPositionInLin() + ", length: "+this.getNodeLength()+"\n";

        //TODo add useful outlineName for class
        String outlineString = "";
        for (MethodNode method : methods) {
            outlineString += method.getOutlineForChildElements();
        }
        return outlineString;
    }

    @Override
    public String getOutlineLink() {
        return "<a href='#" + startLine + "'>" + name + "</a>";
    }

    @Override
    public void addCompoundNodesToList(List<CompoundNode> nodes) {
        for (MethodNode method : methods) {
            nodes.add(method);
            method.addCompoundNodesToList(nodes);
        }
    }
}
