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
package org.codesearch.commons.plugins.javacodeanalyzerplugin.ast;

import java.util.List;
import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.ExternalUsage;

/**
 * Represents the usage(call) of a method that is defined in a different file(class).
 * The targetNodeName in this case is the method's class name.
 * @author David Froehlich
 */
public class ExternalMethodUsage extends ExternalUsage {
    private static final long serialVersionUID = 1L;

    private List<String> parameters;

    public ExternalMethodUsage(int startPositionInLine, int startLine, int length, String replacedString,
            String targetNodeName, List<String> parameters) {
        super(startPositionInLine, startLine, length, replacedString, targetNodeName);
        this.parameters = parameters;
    }

    public List<String> getParameters() {
        return parameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findTargetLineNumber(AstNode node) {
        for (AstNode currentNode : node.getChildNodes()) {
            if (currentNode instanceof MethodNode) {
                MethodNode methodNode = (MethodNode) currentNode;
                if (super.replacedString.equals(methodNode.getName()) && parameters.size() == methodNode.getParameters().size()) {
                    return methodNode.getStartLine();
                }
            } else if (currentNode.getChildNodes() != null && !currentNode.getChildNodes().isEmpty()) {
                return findTargetLineNumber(currentNode);
            }
        }
        return -1;
    }
}
