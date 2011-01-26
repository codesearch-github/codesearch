
package org.codesearch.searcher.client.ui.fileview.sidebar;

import org.codesearch.searcher.shared.SidebarNode;

import com.google.gwt.user.client.ui.Composite;

/**
 * A simple sidebar widget that can display a tree {@link SidebarNode}.
 * @author Samuel Kogler
 */
public abstract class Sidebar extends Composite {

    /**
     * Sets the title of the sidebar.
     * @param title The title
     */
    public abstract void setSidebarTitle(String title);

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
