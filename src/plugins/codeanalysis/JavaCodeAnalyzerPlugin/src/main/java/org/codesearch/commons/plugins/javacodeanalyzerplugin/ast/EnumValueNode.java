/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.javacodeanalyzerplugin.ast;

import java.util.Collections;
import java.util.List;
import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;

/**
 *
 * @author David Froehlich
 */
public class EnumValueNode extends AstNode{

    @Override
    public String getOutlineName() {
        return "";
    }

    @Override
    public boolean showInOutline() {
        return false;
    }

    @Override
    public List<AstNode> getChildNodes() {
        return Collections.emptyList();
    }

    @Override
    public String getModifiers() {
        return "";
    }

}
