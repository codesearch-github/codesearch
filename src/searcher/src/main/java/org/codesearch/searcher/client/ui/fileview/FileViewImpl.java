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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import org.codesearch.searcher.client.ui.searchview.SearchPlace;

/**
 * Implementation of the File View.
 * TODO clean this class up a bit
 * @author Samuel Kogler
 */
public class FileViewImpl extends Composite implements FileView {

    private static FileViewImpl instance;

    interface FileViewUiBinder extends UiBinder<Widget, FileViewImpl> {
    }

    interface MyStyle extends CssResource {

        String hidden();
    }
    @UiField
    MyStyle style;
    private static FileViewUiBinder uiBinder = GWT.create(FileViewUiBinder.class);
    private Presenter presenter;
    @UiField
    FlowPanel fileContentContainer;
    @UiField
    FlowPanel lineNumbersContainer;
    @UiField
    ScrollPanel scrollWrapper;
    @UiField
    Label pathField;
    @UiField
    Label repositoryField;
    @UiField
    HasClickHandlers backButton;
    @UiField
    DivElement focusDiv;
    /** Handler for keyboard shortcuts. */
    private HandlerRegistration goToLineHandlerRegistration;

    int lineCount = 0;

    private FileViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public static FileView getInstance() {
        if (instance == null) {
            instance = new FileViewImpl();
            exportGoToLine();
        }
        return instance;
    }

    @UiHandler("backButton")
    void onBack(ClickEvent event) {
        presenter.goTo(new SearchPlace());
    }

    @UiHandler("goToLineButton")
    void onGoToLine(ClickEvent event) {
        goToLineWithDialog();
    }

    @Override
    public void setPresenter(FileView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setFileContent(String fileContent, boolean binary) {
        fileContentContainer.clear();
        lineNumbersContainer.clear();
        lineNumbersContainer.setVisible(!binary);
        showFocusDiv(!binary);
        lineCount = 0;

        if (!binary) {
            String[] fileContentArray = fileContent.split("\n");
            lineCount = fileContentArray.length;
            goToLine(1);
            for (int i = 0; i < fileContentArray.length; i++) {
                Label lineNumber = new Label(String.valueOf(i + 1));
                lineNumber.addClickHandler(new LineNumberClickHandler(i + 1));
                lineNumbersContainer.add(lineNumber);
            }
        }

        fileContent = "<pre>" + fileContent + "</pre>";

        fileContentContainer.add(new HTML(fileContent));
        scrollWrapper.onResize();
    }

    @Override
    public void setFilePath(String filePath) {
        pathField.setText(filePath);
    }

    @Override
    public void setRepository(String repository) {
        repositoryField.setText(repository);
    }

    private void showFocusDiv(boolean show) {
        if (show) {
            focusDiv.removeClassName(style.hidden());
        } else {
            focusDiv.addClassName(style.hidden());
        }
    }

    public void goToLine(int lineNumber) {
        if (lineNumber > 0 && lineNumber <= lineCount) {
            focusDiv.setAttribute("style", "top: " + (lineNumber - 1) * 15 + "px");
        }
        focusDiv.scrollIntoView();
    }

    private class LineNumberClickHandler implements ClickHandler {

        private int targetLine;

        public LineNumberClickHandler(int targetLine) {
            this.targetLine = targetLine;
        }

        @Override
        public void onClick(ClickEvent event) {
            goToLine(targetLine);
        }
    }

    private void goToLineWithDialog() {
        String input = Window.prompt("Go to line:", "");
        try {
            int lineNumber = Integer.parseInt(input);
            goToLine(lineNumber);
        } catch (NumberFormatException ex) {
            // TODO insert error handling
        }
    }

    @Override
    public void connectEventHandlers() {
        goToLineHandlerRegistration = Event.addNativePreviewHandler(new NativePreviewHandlerImpl());
    }

    @Override
    public void disconnectEventHandlers() {
        goToLineHandlerRegistration.removeHandler();
    }

    public static void staticGoToLine(int lineNumber) {
        if (instance == null) {
            instance = new FileViewImpl();
        }
        instance.goToLine(lineNumber);
    }

    public static native void exportGoToLine()/*-{
    $wnd.goToLine = $entry(@org.codesearch.searcher.client.ui.fileview.FileViewImpl::staticGoToLine(I));
    }-*/;

    private class NativePreviewHandlerImpl implements NativePreviewHandler {

        public NativePreviewHandlerImpl() {
        }

        @Override
        public void onPreviewNativeEvent(NativePreviewEvent event) {
            switch (event.getNativeEvent().getCharCode()) {
                case 'g':
                    goToLineWithDialog();
                    break;
                case 'b':
                    onBack(null);
                    break;
            }
        }
    }
}
