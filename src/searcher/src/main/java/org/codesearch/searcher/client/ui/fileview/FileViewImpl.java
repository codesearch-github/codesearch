package org.codesearch.searcher.client.ui.fileview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
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
    ScrollPanel fileContent;
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
    public void setFileContent(String fileContent) {
        this.fileContent.clear();
        //TODO after inserting highlighting, re-enable this
//        this.fileContent.add(new HTML("<pre>" + fileContent + "</pre>"));
        this.fileContent.add(new HTML("<pre>" + fileContent + "</pre>"));
        this.fileContent.onResize();
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
