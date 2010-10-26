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
public class FileNode extends ASTElement {
    private List<ClassNode> classes = new LinkedList<ClassNode>();

    public List<ClassNode> getClasses() {
        return classes;
    }

    public void setClasses(List<ClassNode> classes) {
        this.classes = classes;
    }

    @Override
    public String getOutlineForChildElements(){
        String outlineString = "File: "+this.getName()+" from: "+this.getDeclarationPosition() + ", length: "+this.getNodeLength()+"\n";
        for(ClassNode currentClass : classes){
            outlineString += currentClass.getOutlineForChildElements();
        }
        return outlineString;
    }
}
