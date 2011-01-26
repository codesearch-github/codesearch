package org.codesearch.commons.plugins.codeanalyzing.xml.ast;

import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.CompoundNode;

/**
 * An XML AST node.
 * @author Samuel Kogler
 */
public class XmlNode extends CompoundNode {

    private List<XmlNode> xmlNodes = new LinkedList<XmlNode>();

    public List<XmlNode> getXmlNodes() {
        return xmlNodes;
    }

    /** {@inheritDoc} */
    @Override
    public String getOutlineName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public boolean showInOutline() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public List<AstNode> getChildNodes() {
        List<AstNode> astNodes = new LinkedList<AstNode>();
        astNodes.addAll(xmlNodes);

        return astNodes;
    }

    @Override
    public String getModifiers() {
        return "";
    }
}
