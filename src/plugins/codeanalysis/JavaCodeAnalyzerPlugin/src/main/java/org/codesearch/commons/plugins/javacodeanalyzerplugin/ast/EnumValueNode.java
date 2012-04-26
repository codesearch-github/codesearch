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

import java.util.Collections;
import java.util.List;

import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;

/**
 * ASTNode that represents the value of an enum in the source code
 * @author David Froehlich
 */
public class EnumValueNode extends AstNode {

    /** {@inheritDoc} */
    @Override
    public String getOutlineName() {
        return "";
    }

    /** {@inheritDoc} */
    @Override
    public boolean showInOutline() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public List<AstNode> getChildNodes() {
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
    @Override
    public String getModifiers() {
        return "";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EnumValueNode) {
            return super.equals(obj);
        }
        return false;
    }
}
