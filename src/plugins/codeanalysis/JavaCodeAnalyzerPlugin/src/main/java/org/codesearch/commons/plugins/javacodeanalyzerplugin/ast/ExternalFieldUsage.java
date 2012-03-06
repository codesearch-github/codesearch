package org.codesearch.commons.plugins.javacodeanalyzerplugin.ast;

import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.ExternalUsage;

/**
 * Represents a usage of a field defined in a different file.
 * Uses the replacedString attribute as the field name and the targetNodeName as
 * the parent node name to find the occurrence within the target file.
 * @author Samuel Kogler
 */
public class ExternalFieldUsage extends ExternalUsage {
    private static final long serialVersionUID = 1L;

    public ExternalFieldUsage(int startPositionInLine, int startLine, int length, String replacedString, String targetNodeName) {
        super(startPositionInLine, startLine, length, replacedString, targetNodeName);
    }

    @Override
    public int findTargetLineNumber(AstNode node) {
        for (AstNode currentNode : node.getChildNodes()) {
            if (replacedString.equals(currentNode.getName())) {
                return currentNode.getStartLine();
            } else if (currentNode.getChildNodes() != null && !currentNode.getChildNodes().isEmpty()) {
                return findTargetLineNumber(currentNode);
            }
        }
        return -1;
    }
}
