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

package org.codesearch.searcher.shared;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.codesearch.searcher.client.ui.fileview.FileViewImpl;

/**
 *
 * @author Samuel Kogler
 */
public class OutlineNode implements SidebarNode, Serializable {
    private List<SidebarNode> childs;
    private String name;
    private int startLine;
    private String cssClasses;

    @Override
    public String getCssClasses() {
        return cssClasses;
    }

    public void setCssClasses(String cssClasses) {
        this.cssClasses = cssClasses;
    }
    
    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    @Override
    public List<SidebarNode> getChilds() {
        if(childs == null) {
            return Collections.EMPTY_LIST;
        }
        return childs;
    }

    public void setChilds(List<SidebarNode> childs) {
        this.childs = childs;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void onClick() {
        FileViewImpl.staticGoToLine(startLine);
    }

    @Override
    public String getDisplayText() {
        return name;
    }

}
