package org.codesearch.searcher.client.ui.fileview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
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
    SimplePanel fileContentPanel;

    @UiField
    Label urlField;

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
       fileContentPanel.add(new HTML("<pre>\n" + fileContent + "</pre>"));
    }
}
