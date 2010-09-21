package org.codesearch.searcher.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author Samuel Kogler
 */
public class FileViewWidget extends Composite {

    private static FileViewWidgetUiBinder uiBinder = GWT.create(FileViewWidgetUiBinder.class);

    interface FileViewWidgetUiBinder extends UiBinder<Widget, FileViewWidget> {
    }

    public FileViewWidget() {
        initWidget(uiBinder.createAndBindUi(this));
    }
}