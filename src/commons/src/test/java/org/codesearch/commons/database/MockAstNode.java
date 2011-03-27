package org.codesearch.commons.database;

import java.util.List;
import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;

/**
 *
 * @author Samuel Kogler
 */
public class MockAstNode extends AstNode {

    @Override
    public String getOutlineName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean showInOutline() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<AstNode> getChildNodes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getModifiers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
