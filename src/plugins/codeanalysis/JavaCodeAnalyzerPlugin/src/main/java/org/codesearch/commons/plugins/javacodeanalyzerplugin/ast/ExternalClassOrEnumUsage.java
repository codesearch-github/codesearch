/**
 * Copyright 2010 David Froehlich   <david.froehlich@businesssoftware.at>,
 *                Samuel Kogler     <samuel.kogler@gmail.com>,
 *                Stephan Stiboller <stistc06@htlkaindorf.at>
 *
 * This file is part of Codesearch.
 *
 * Codesearch is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Codesearch is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Codesearch.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.codesearch.commons.plugins.javacodeanalyzerplugin.ast;


import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.ExternalUsage;

/**
 * Represents the usage of either a class or enum in the source code
 * @author David Froehlich
 */
public class ExternalClassOrEnumUsage extends ExternalUsage {

    /** {@inheritDoc } */
    public ExternalClassOrEnumUsage(int startPositionInLine, int startLine, int length, String replacedString, String targetClassName) {
        super(startPositionInLine, startLine, length, replacedString, targetClassName);
    }

    /** {@inheritDoc} */
    @Override
    public void resolveLink(String targetFilePath, AstNode ast) {
        super.setTargetFilePath(targetFilePath);
        super.setReferenceLine(-1);
    }
}
