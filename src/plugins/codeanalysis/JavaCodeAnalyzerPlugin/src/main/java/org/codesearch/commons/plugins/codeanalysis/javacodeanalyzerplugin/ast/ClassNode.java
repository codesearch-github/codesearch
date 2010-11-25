/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.codeanalysis.javacodeanalyzerplugin.ast;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.codesearch.commons.plugins.codeanalyzing.ast.CompoundNode;

/**
 * ASTNode that represents a class in the source code
 * @author David Froehlich
 */
public class ClassNode extends CompoundNode{
    private List<MethodNode> methods = new LinkedList<MethodNode>();

    public List<MethodNode> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodNode> methods) {
        this.methods = methods;
    }
    
    public String getOutlineForChildElements() {
        String outlineString = "  Class definition: "+ this.getName() + " from: "+this.getStartPosition() + ", length: "+this.getNodeLength()+"\n";
        for(MethodNode method : methods){
            outlineString += method.getOutlineForChildElements();
        }
        return outlineString;
    }

    @Override
    public String getOutlineLink() {
        return "<a href='#"+startLine+"'>"+name+"</a>";
    }

    @Override
    public void addCompoundNodesToMap(Map<Integer, CompoundNode> map) {
        for(MethodNode method : methods){
            map.put(method.getStartPosition(), method);
            method.addCompoundNodesToMap(map);
        }
    }
}
