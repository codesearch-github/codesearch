/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.codeanalyzing.ast;

/**
 *
 * @author David Froehlich
 */
public class Variable extends ASTElement {
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
