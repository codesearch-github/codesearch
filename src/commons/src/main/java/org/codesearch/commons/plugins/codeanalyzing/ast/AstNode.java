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
package org.codesearch.commons.plugins.codeanalyzing.ast;

import java.io.Serializable;
import java.util.List;

/**
 * Superclass for all ASTNodes used for code analysis.
 * @author David Froehlich
 */
public abstract class AstNode implements Serializable, Comparable<AstNode> {

    /** . */
    private static final long serialVersionUID = -5265705991596746163L;
    protected int startLine;
    protected int startPositionInLine;
    protected int startPositionAbsolute;
    protected String name;
    protected Visibility visibility;
    /** the start line of the parent node */
    protected int parentLineDeclaration;

    /**
     * Returns the text to be displayed as name in the outline.
     * @return
     */
    public abstract String getOutlineName();

    /**
     * Whether this {@link AstNode} should be shown in the outline.
     * @return
     */
    public abstract boolean showInOutline();

    /**
     * returns a list of all child nodes
     * @return all child nodes
     */
    public abstract List<AstNode> getChildNodes();

    public abstract String getModifiers();

    @Override
    public int compareTo(AstNode other) {
        if (this.startLine == other.startLine) {
            return this.startPositionInLine - other.startPositionInLine;
        }
        return startLine - other.startLine;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getStartPositionInLine() {
        return startPositionInLine;
    }

    public void setStartPositionInLine(int startPositionInLine) {
        this.startPositionInLine = startPositionInLine;
    }

    public int getStartPositionAbsolute() {
        return startPositionAbsolute;
    }

    public void setStartPositionAbsolute(int startPositionAbsolute) {
        this.startPositionAbsolute = startPositionAbsolute;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public int getParentLineDeclaration() {
        return parentLineDeclaration;
    }

    public void setParentLineDeclaration(int parentLineDeclaration) {
        this.parentLineDeclaration = parentLineDeclaration;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AstNode) {
            AstNode other = (AstNode) obj;
            if (other.getStartLine() == this.getStartLine() && other.getStartPositionInLine() == this.getStartPositionInLine()) {
                try {
                    if ((other.getName() == null && this.getName() == null) || other.getName().equals(this.getName())) {

                        if ((this.getVisibility() == null && other.getVisibility() == null) || this.visibility == other.getVisibility()) {
                            if (other.getChildNodes().equals(this.getChildNodes())) {
                                return true;
                            }
                        }

                    }
                } catch (NullPointerException ex) {
                    //in case only one of the modifiers was null, return false 4 lines later
                }
            }
        }
        return false;
    }
}
