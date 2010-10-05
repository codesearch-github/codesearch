package org.codesearch.searcher.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author Samuel Kogler
 */
public class FileViewWidget extends Composite {

    private static FileViewWidgetUiBinder uiBinder = GWT.create(FileViewWidgetUiBinder.class);

    interface FileViewWidgetUiBinder extends UiBinder<Widget, FileViewWidget> {
    }
    
    @UiField
    HTMLPanel fileContent;

    public FileViewWidget() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setFileContent(String fileContent) {
        this.fileContent.clear();
        this.fileContent.add(new HTML(fileContent));
    }
}
