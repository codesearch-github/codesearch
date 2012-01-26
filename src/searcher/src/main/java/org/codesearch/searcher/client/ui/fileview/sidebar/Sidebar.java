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


package org.codesearch.searcher.client.ui.fileview.sidebar;

import com.google.gwt.user.client.ui.Composite;
import org.codesearch.searcher.shared.SidebarNode;

/**
 * A simple sidebar widget that can display a tree {@link SidebarNode}.
 * @author Samuel Kogler
 */
public abstract class Sidebar extends Composite {

    /**
     * Clears all elements from the sidebar.
     */
    public abstract void clear();

    /**
     * Adds a {@link SidebarNode} to the sidebar.
     * @param SidebarNode
     */
    public abstract void add(SidebarNode sidebarNode);

    /**
     * Expands all tree items.
     */
    public abstract void expandAll();
}
