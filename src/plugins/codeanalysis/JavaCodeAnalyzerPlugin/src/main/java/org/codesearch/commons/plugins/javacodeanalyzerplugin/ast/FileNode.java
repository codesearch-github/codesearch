/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.javacodeanalyzerplugin.ast;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.codesearch.commons.plugins.codeanalyzing.ast.CompoundNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.Node;

/**
 * AST element that represents a class in the source code file
 * @author David Froehlich
 */
public class FileNode extends CompoundNode {
    private List<ClassNode> classes = new LinkedList<ClassNode>();

    public List<ClassNode> getClasses() {
        return classes;
    }

    public void setClasses(List<ClassNode> classes) {
        this.classes = classes;
    }
    
    public String getOutlineForChildElements(){
        String outlineString = "File: "+this.getName()+" from: "+this.getStartPositionInLine() + ", length: "+this.getNodeLength()+"\n";
        for(ClassNode currentClass : classes){
            outlineString += currentClass.getOutlineForChildElements();
        }
        return outlineString;
    }

    @Override
    public String getOutlineLink() {
        return "";
    }

    @Override
    public void addCompoundNodesToList(List<CompoundNode> nodes) {
        for(ClassNode clazz : classes){
            nodes.add(clazz);
            clazz.addCompoundNodesToList(nodes);
        }
    }
}
