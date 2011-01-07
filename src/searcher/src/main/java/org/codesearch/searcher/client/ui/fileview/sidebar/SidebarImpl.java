package org.codesearch.searcher.client.ui.fileview.sidebar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import org.codesearch.searcher.shared.SidebarNode;

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
    @UiField
    Label titleLabel;

    public SidebarImpl() {
        initWidget(uiBinder.createAndBindUi(this));
        sidebarTree.addSelectionHandler(new SelectionHandler<TreeItem>() {

            @Override
            public void onSelection(SelectionEvent<TreeItem> event) {
                ( (SidebarNode) event.getSelectedItem().getUserObject() ).onClick();
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
        treeItem.setState(true);
        for (SidebarNode s : sidebarNode.getChilds()) {
            if (s != null) {
                treeItem.addItem(convertSidebarNodeToTreeItem(s));
            }
        }
        return treeItem;
    }

    @Override
    public void setSidebarTitle(String title) {
        this.titleLabel.setText(title);
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
