/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.codeanalysis.javacodeanalyzerplugin.ast;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.codesearch.commons.plugins.codeanalysis.javacodeanalyzerplugin.ast.ClassNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.CompoundNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.CompoundNode;

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
        String outlineString = "File: "+this.getName()+" from: "+this.getStartPosition() + ", length: "+this.getNodeLength()+"\n";
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
    public void addCompoundNodesToMap(Map<Integer, CompoundNode> map) {
        for(ClassNode clazz : classes){
            map.put(clazz.getStartPosition(), clazz);
            clazz.addCompoundNodesToMap(map);
        }
    }
}
