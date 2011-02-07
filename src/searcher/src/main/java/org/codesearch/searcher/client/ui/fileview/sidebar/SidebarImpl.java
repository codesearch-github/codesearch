package org.codesearch.searcher.client.ui.fileview.sidebar;

import org.codesearch.searcher.shared.SidebarNode;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * A side bar that can display a tree.
 * @author Samuel Kogler
 */
public class SidebarImpl extends Sidebar {

    private static SidebarUiBinder uiBinder = GWT.create(SidebarUiBinder.class);

    interface SidebarUiBinder extends UiBinder<Widget, SidebarImpl> {
    }
    @UiField
    Tree sidebarTree;

    public SidebarImpl() {
        initWidget(uiBinder.createAndBindUi(this));
        sidebarTree.addSelectionHandler(new SelectionHandler<TreeItem>() {
            @Override
            public void onSelection(SelectionEvent<TreeItem> event) {
                ((SidebarNode) event.getSelectedItem().getUserObject()).onClick();
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void add(SidebarNode sidebarNode) {
        if (sidebarNode != null) {
            sidebarTree.addItem(convertSidebarNodeToTreeItem(sidebarNode));
        }
    }

    private TreeItem convertSidebarNodeToTreeItem(SidebarNode sidebarNode) {
        TreeItem treeItem = new TreeItem(sidebarNode.getDisplayText());
        treeItem.setUserObject(sidebarNode);
        String cssClasses = sidebarNode.getCssClasses();
        if (cssClasses != null) {
            treeItem.getElement().setClassName(cssClasses.toLowerCase());
        }
        treeItem.setState(true);
        for (SidebarNode s : sidebarNode.getChilds()) {
            if (s != null) {
                treeItem.addItem(convertSidebarNodeToTreeItem(s));
            }
        }
        return treeItem;
    }

    /** {@inheritDoc} */
    @Override
    public void clear() {
        sidebarTree.clear();
    }

    @Override
    public void expandAll() {
        for (int i = 0; i < sidebarTree.getItemCount(); i++) {
            expandItem(sidebarTree.getItem(i));
        }
    }

    private void expandItem(TreeItem item) {
        item.setState(true);
        for (int i = 0; i < item.getChildCount(); i++) {
            expandItem(item.getChild(i));
        }
    }
}
