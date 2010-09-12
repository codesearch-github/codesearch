package org.codesearch.searcher.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.codesearch.searcher.client.rpc.SearcherServiceAsync;
import org.codesearch.searcher.client.rpc.SearcherService;
import org.codesearch.searcher.shared.SearchResultDto;

/**
 * Implements the functionality of the search page.
 * Composite class corresponding to the UiBinder template SearchInterface.ui.xml.
 * @author Samuel Kogler
 */
public class SearchInterface extends Composite {

    interface SearchInterfaceUiBinder extends UiBinder<Widget, SearchInterface> {
    }
    private static SearchInterfaceUiBinder uiBinder = GWT.create(SearchInterfaceUiBinder.class);
    private SearcherServiceAsync searcherServiceAsync = GWT.create(SearcherService.class);
    @UiField
    TextBox searchBox;
    @UiField
    Button searchButton;
    @UiField
    Label resultLabel;

    public SearchInterface() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("searchButton")
    void onSearchButton(ClickEvent e) {
        search();
    }

    @UiHandler("searchBox")
    void onSearchBoxKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            searchButton.click();
        }
    }

    private void search() {
        String query = searchBox.getText();
        searcherServiceAsync.doSearch(query, new AsyncCallback<SearchResultDto[]>() {

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Exception calling the search service on the server:\n" + caught);
            }

            @Override
            public void onSuccess(SearchResultDto[] result) {
                Window.alert("Successfully called service:\n" + result);
            }
        });
    }
}
