/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.javacodeanalyzerplugin.ast;

import org.codesearch.commons.plugins.codeanalyzing.ast.Node;

/**
 * ASTElement that represents a variable in the source code
 * @author David Froehlich
 */
public class VariableNode extends Node {
    private String type;
    private Visibility visibility;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }
}
