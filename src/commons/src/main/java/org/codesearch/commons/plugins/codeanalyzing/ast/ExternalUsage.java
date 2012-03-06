/**
 * Copyright 2010 David Froehlich <david.froehlich@businesssoftware.at>, Samuel
 * Kogler <samuel.kogler@gmail.com>, Stephan Stiboller <stistc06@htlkaindorf.at>
 *
 * This file is part of Codesearch.
 *
 * Codesearch is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Codesearch is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Codesearch. If not, see <http://www.gnu.org/licenses/>.
 */
package org.codesearch.commons.plugins.codeanalyzing.ast;

/**
 * Represents a usage in the source code that points outside of the current
 * file.
 *
 * @author David Froehlich
 */
public class ExternalUsage extends Usage {

    /**
     * .
     */
    private static final long serialVersionUID = 8289970633299155779L;
    /**
     * The name of the target node.
     */
    protected String targetNodeName;

    public ExternalUsage() {
    }

    /**
     * creates a new instance of ExternalUsage
     *
     * @param startPositionInLine the column where the usage starts in the line
     * @param startLine the line of the usage
     * @param length the length of the usage in chars
     * @param replacedString the string that is replaced in the line
     * @param targetNodeName the name of the node that the usage points to
     */
    public ExternalUsage(int startPositionInLine, int startLine, int length, String replacedString, String targetNodeName) {
        super.startColumn = startPositionInLine;
        super.startLine = startLine;
        super.length = length;
        super.replacedString = replacedString;
        this.targetNodeName = targetNodeName;
    }

    public String getTargetNodeName() {
        return targetNodeName;
    }

    public void setTargetNodeName(String targetClassName) {
        this.targetNodeName = targetClassName;
    }

    /**
     * Returns the line number of the target of this usage in the file
     * corresponding to the given AST. Subclasses may override this method to
     * handle programming language specific node matching.
     *
     * The default implementation is that the names of the nodes must match.
     *
     * @param node The given AST (the root node)
     * @return The line number of the target of this usage, or -1 if not found
     * or not implemented.
     */
    public int findTargetLineNumber(AstNode node) {
        for (AstNode currentNode : node.getChildNodes()) {
            if (targetNodeName.equals(currentNode.getName())) {
                return currentNode.getStartLine();
            } else if (currentNode.getChildNodes() != null && !currentNode.getChildNodes().isEmpty()) {
                return findTargetLineNumber(currentNode);
            }
        }
        return -1;
    }
}
