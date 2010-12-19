package org.codesearch.searcher.client.ui.fileview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import org.codesearch.searcher.client.ui.searchview.SearchPlace;

/**
 * Implementation of the File View.
 * @author Samuel Kogler
 */
public class FileViewImpl extends Composite implements FileView {

    interface FileViewUiBinder extends UiBinder<Widget, FileViewImpl> {
    }
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

    public FileViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("backButton")
    void onBack(ClickEvent event) {
        presenter.goTo(new SearchPlace());
    }

    @Override
    public void setPresenter(FileView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setFileContent(String fileContent, boolean binary) {
        //FIXME chek if this code runs twice
        fileContentContainer.clear();
        lineNumbersContainer.clear();
        lineNumbersContainer.setVisible(!binary);

        if (!binary) {
            String[] fileContentArray = fileContent.split("\n");
            StringBuilder fileContentBuilder = new StringBuilder();
            fileContentBuilder.append("<pre>");
            for (int i = 0; i < fileContentArray.length; i++) {
                //TODO change back once binary recognition works
                fileContentBuilder.append(fileContentArray[i]).append("\n");
//                fileContentBuilder.append("<span id=\"line").append(i + 1).append("\">").append(fileContentArray[i]).append("</span>\n");
                lineNumbersContainer.add(new Hyperlink(String.valueOf(i + 1), "#line" + i));
            }
            fileContentBuilder.append("</pre>");
            fileContent = fileContentBuilder.toString();
        }

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
}
