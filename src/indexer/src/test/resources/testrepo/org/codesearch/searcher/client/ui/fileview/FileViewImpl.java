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
package org.codesearch.searcher.client.ui.fileview;

import java.util.List;

import org.codesearch.searcher.client.ui.fileview.sidebar.Sidebar;
import org.codesearch.searcher.client.ui.fileview.sidebar.SidebarImpl;
import org.codesearch.searcher.shared.OutlineNode;
import org.codesearch.searcher.shared.SidebarNode;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.LinkedList;
import org.codesearch.searcher.client.ClientFactory;

/**
 * Implementation of the File View.
 * @author Samuel Kogler
 */
public class FileViewImpl extends Composite implements FileView {
    //TODO cleanup this class

    interface FileViewUiBinder extends UiBinder<Widget, FileViewImpl> {
    }

    interface MyStyle extends CssResource {

        String hidden();
    }
    @UiField
    MyStyle style;
    @UiField
    FlowPanel fileContentContainer;
    @UiField
    FlowPanel lineNumbersContainer;
    @UiField
    SplitLayoutPanel splitWrapper;
    @UiField
    ScrollPanel scrollWrapper;
    @UiField
    Label filenameLabel;
    @UiField
    Label repositoryField;
    @UiField
    HasClickHandlers sidebarButton;
    @UiField
    DivElement focusDiv;
    @UiField
    TabLayoutPanel sidebarTabPanel;
    /** UiBinder template. */
    private static FileViewUiBinder uiBinder = GWT.create(FileViewUiBinder.class);
    /** The presenter for this view. */
    private Presenter presenter;
    /** Handler for keyboard shortcuts. */
    private HandlerRegistration keyboardShortcutHandlerRegistration;
    /** Number of lines of the displayed file */
    private int lineCount = 0;
    /** Whether or not the focus line div is visible. */
    private boolean focusDivVisible;
    private String searchTerm;
    /** Whether or not the sidebar is visible. */
    private boolean sidebarVisible;
    /** The list of displayed sidebars in this view. */
    private List<Sidebar> shownSidebars = new LinkedList<Sidebar>();

    public FileViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
        exportJSFunctions();
    }

    @UiHandler("goToLineButton")
    void onGoToLine(ClickEvent event) {
        goToLineWithDialog();
    }

    @UiHandler("sidebarButton")
    void onSidebar(ClickEvent event) {
        toggleSidebar();
    }

    /** {@inheritDoc} */
    @Override
    public void cleanup() {
        fileContentContainer.clear();
        lineNumbersContainer.clear();
        shownSidebars.clear();
        updateSidebar();
        lineCount = 0;
        sidebarVisible = false;
        showSidebar(sidebarVisible);
        showFocusDiv(false);
    }

    /** {@inheritDoc} */
    @Override
    public void setPresenter(FileView.Presenter presenter) {
        this.presenter = presenter;
    }

    /** {@inheritDoc} */
    @Override
    public Presenter getPresenter() {
        return presenter;
    }

    /** {@inheritDoc} */
    @Override
    public void setFileContent(String fileContent, boolean binary) {
        lineNumbersContainer.setVisible(!binary);
        showFocusDiv(false);

        if (!binary) {
            lineCount = fileContent.split("\n").length;

            for (int i = 0; i < lineCount; i++) {
                Label lineNumber = new Label(String.valueOf(i + 1));
                lineNumber.addClickHandler(new LineNumberClickHandler(i + 1));
                lineNumbersContainer.add(lineNumber);
            }
            highlightSearchTerm(fileContent);
        }

        fileContent = "<pre id='fileContent'>" + fileContent + "</pre>";

        HTML html = new HTML(fileContent);
        fileContentContainer.add(html);

        highlight("#fileContent", searchTerm);
    }

    private native void highlight(String selector, String term)/*-{
    $wnd.jQuery(selector).highlight(term);
    }-*/;

    /** {@inheritDoc} */
    @Override
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    /**
     * Highlights the search term in the given file content
     * @param fileContent
     */
    public void highlightSearchTerm(String fileContent) {
        //TODO implement
    }

    /** {@inheritDoc} */
    @Override
    public void setOutline(List<OutlineNode> outline) {
        if (outline != null) {
            Sidebar sidebar = new SidebarImpl();
            sidebar.setTitle("Outline");
            for (SidebarNode s : outline) {
                sidebar.add(s);
            }
            sidebar.expandAll();
            shownSidebars.add(sidebar);
            updateSidebar();
            if (!sidebarVisible) {
                toggleSidebar();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setFilePath(String filePath) {
        if (filePath != null) {
            String fileName = filePath;
            if (fileName.contains("/")) {
                fileName = fileName.substring(filePath.lastIndexOf('/') + 1);
            }
            filenameLabel.setText(fileName);
            filenameLabel.setTitle(filePath);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setRepository(String repository) {
        repositoryField.setText(repository);
    }

    /** {@inheritDoc} */
    @Override
    public void goToLine(int lineNumber) {
        if (!focusDivVisible) {
            showFocusDiv(true);
        }
        if (lineNumber > 0 && lineNumber <= lineCount) {
            focusDiv.setAttribute("style", "top: " + (lineNumber - 1) * 15 + "px");
        }
        focusDiv.scrollIntoView();
    }

    /** {@inheritDoc} */
    @Override
    public void connectEventHandlers() {
        keyboardShortcutHandlerRegistration = Event.addNativePreviewHandler(new NativePreviewHandlerImpl());
    }

    /** {@inheritDoc} */
    @Override
    public void disconnectEventHandlers() {
        keyboardShortcutHandlerRegistration.removeHandler();
    }

    public static void staticGoToUsage(int usageId) {
        ClientFactory.getDefaultFactory().getFileView().getPresenter().goToUsage(usageId);
    }

    public static void staticGoToLine(int lineNumber) {
        ClientFactory.getDefaultFactory().getFileView().goToLine(lineNumber);
    }

    /**
     * Exports static functions to JavaScript so they can be used from HTML code.
     */
    public static native void exportJSFunctions()/*-{
    $wnd.goToLine = $entry(@org.codesearch.searcher.client.ui.fileview.FileViewImpl::staticGoToLine(I));
    $wnd.goToUsage = $entry(@org.codesearch.searcher.client.ui.fileview.FileViewImpl::staticGoToUsage(I));
    }-*/;

    private void toggleSidebar() {
        sidebarVisible = !sidebarVisible;
        showSidebar(sidebarVisible);
    }

    private void showSidebar(boolean show) {
        if (show) {
            splitWrapper.clear();
            splitWrapper.addWest(sidebarTabPanel, 300);
            splitWrapper.add(scrollWrapper);
        } else {
            splitWrapper.remove(sidebarTabPanel);
        }
        splitWrapper.animate(100);
    }

    private void updateSidebar() {
        sidebarTabPanel.clear();
        for (Sidebar sidebar : shownSidebars) {
            sidebarTabPanel.add(sidebar, sidebar.getTitle());
        }
    }

    /**
     * Shows an input dialog for the go to line feature.
     */
    private void goToLineWithDialog() {
        String input = Window.prompt("Go to line:", "");
        try {
            int lineNumber = Integer.parseInt(input);
            goToLine(lineNumber);
        } catch (NumberFormatException ex) {
        }
    }

    /**
     * Shows or hides the focused line div that highlights the focused line.
     * @param show Whether to show the div
     */
    private void showFocusDiv(boolean show) {
        focusDivVisible = show;
        if (show) {
            focusDiv.removeClassName(style.hidden());
        } else {
            focusDiv.addClassName(style.hidden());
        }
    }

    /**
     * Handler class that intercepts native javascript events. Used for global hotkeys.
     */
    private class NativePreviewHandlerImpl implements NativePreviewHandler {

        public NativePreviewHandlerImpl() {
        }

        /** {@inheritDoc} */
        @Override
        public void onPreviewNativeEvent(NativePreviewEvent event) {
            switch (event.getNativeEvent().getCharCode()) {
                case 'g':
                    event.consume();
                    goToLineWithDialog();
                    break;
            }
        }
    }

    /**
     * Click handler that is used by line numbers to call the goToLine function.
     */
    private class LineNumberClickHandler implements ClickHandler {

        private int targetLine;

        public LineNumberClickHandler(int targetLine) {
            this.targetLine = targetLine;
        }

        /** {@inheritDoc} */
        @Override
        public void onClick(ClickEvent event) {
            goToLine(targetLine);
        }
    }
}
