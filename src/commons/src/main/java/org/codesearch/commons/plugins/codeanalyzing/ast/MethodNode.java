/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.codeanalyzing.ast;

import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang.NotImplementedException;

/**
 *
 * @author David Froehlich
 */
public class MethodNode extends ASTElement{
    private Visibility visibility;
    private List<Variable> parameters = new LinkedList<Variable>();
    private String returnType;

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public List<Variable> getParameters() {
        return parameters;
    }

    public void setParameters(List<Variable> parameters) {
        this.parameters = parameters;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public String getOutlineLink(){
        //TODO replace with real method
        String parameterString = "";
        String returnString = "";
        for(Variable v : parameters){
            parameterString += v.getType() + " " + v.getName()+",";
        }
        if(parameterString.length() > 0){
            parameterString = parameterString.substring(0, parameterString.length()-1);
        }
        if(returnType != null){
            returnString = ": "+returnType;
        }
        String link = this.getName()+"("+parameterString+")"+returnString + " from " + super.getDeclarationPosition() + ", length: "+super.getNodeLength();
        return link;
    }
}
