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
    private List<ASTElement> nodes = new LinkedList<ASTElement>();

    public List<ASTElement> getNodes() {
        return nodes;
    }

    public void setNodes(List<ASTElement> nodes) {
        this.nodes = nodes;
    }
}
