/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.javacodeanalyzerplugin.ast;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.CompoundNode;

/**
 *
 * @author David Froehlich
 */
public class EnumNode extends CompoundNode{
    private List<EnumValueNode> values = new LinkedList<EnumValueNode>();

    public List<EnumValueNode> getValues() {
        return values;
    }

    public void setValues(List<EnumValueNode> values) {
        this.values = values;
    }

    public EnumNode() {
    }

    @Override
    public String getOutlineName() {
        return super.name;
    }

    @Override
    public boolean showInOutline() {
        return true;
    }

    @Override
    public List<AstNode> getChildNodes() {
        List<AstNode> childNodes = new LinkedList<AstNode>();
        childNodes.addAll(values);
        return childNodes;
    }

    @Override
    public String getModifiers() {
        return "enum " + visibility.toString().toLowerCase();
    }
}
