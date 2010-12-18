/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.javacodeanalyzerplugin.ast;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.codesearch.commons.plugins.codeanalyzing.ast.CompoundNode;

/**
 * ASTElement that represents a method in the source code
 * @author David Froehlich
 */
public class MethodNode extends CompoundNode {

    private Visibility visibility;
    private List<VariableNode> parameters = new LinkedList<VariableNode>();
    private String returnType;
    private boolean constructor;

    public boolean isConstructor() {
        return constructor;
    }

    public void setConstructor(boolean constructor) {
        this.constructor = constructor;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public List<VariableNode> getParameters() {
        return parameters;
    }

    public void setParameters(List<VariableNode> parameters) {
        this.parameters = parameters;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public String getOutlineForChildElements() {
        String parameterString = "";
        String returnString = "";
        for (VariableNode v : parameters) {
            parameterString += v.getType() + " " + v.getName() + ",";
        }
        if (parameterString.length() > 0) {
            parameterString = parameterString.substring(0, parameterString.length() - 1);
        }
        if (returnType != null) {
            returnString = ": " + returnType;
        }
        String outlineString = "";
        if (constructor) {
            outlineString = "    Constructor declaration " + this.getName() + "(" + parameterString + ") from " + this.getStartPosition() + ", length: " + this.getNodeLength() + "\n";
        } else {
            outlineString = "    Method declaration " + this.getName() + "(" + parameterString + ")" + returnString + " from " + super.getStartPosition() + ", length: " + super.getNodeLength() + "\n";
        }
        return outlineString;
    }

    @Override
    public String getOutlineLink() {
        String parameterString = "";
        String returnString = "";
        String visibilityString = "";
        switch (visibility) {
            case private_vis:
                visibilityString = "private ";
                break;
            case public_vis:
                visibilityString = "public ";
                break;
            case protected_vis:
                visibilityString = "protected ";
                break;
        }
        for (VariableNode v : parameters) {
            parameterString += v.getType() + " " + v.getName() + ",";
        }
        if (parameterString.length() > 0) {
            parameterString = parameterString.substring(0, parameterString.length() - 1);
        }
        if (returnType != null) {
            returnString = ": " + returnType;
        }
        String outlineString = "<a href='#" + startLine + "'>"  + visibilityString + this.getName() + "(" + parameterString + ")";
        if (!constructor) {
            outlineString += returnString;
        }
        outlineString += "</a>";
        return outlineString;
    }

    @Override
    public void addCompoundNodesToMap(Map<Integer, CompoundNode> map) {
    }
}
