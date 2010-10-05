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
package org.codesearch.searcher.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import java.util.LinkedList;
import java.util.List;
import org.codesearch.searcher.client.rpc.SearcherServiceAsync;
import org.codesearch.searcher.client.rpc.SearcherService;
import org.codesearch.searcher.shared.InvalidIndexLocationException;
import org.codesearch.searcher.shared.SearchResultDto;

/**
 * Implements the functionality of the search page.
 * Composite class corresponding to the UiBinder template SearchInterface.ui.xml.
 * @author Samuel Kogler
 */
//TODO refactor to make use of MVP pattern.
public class SearchInterface extends Composite {

    interface SearchInterfaceUiBinder extends UiBinder<Widget, SearchInterface> {
    }
    private static SearchInterfaceUiBinder uiBinder = GWT.create(SearchInterfaceUiBinder.class);
    private SearcherServiceAsync searcherServiceAsync = GWT.create(SearcherService.class);
    //TODO workaround, refactor after GWT2.1 release
    @UiField(provided = true)
    CellTable<SearchResultDto> resultTable = new CellTable<SearchResultDto>(15);
    @UiField
    SimplePager resultTablePager;
    @UiField
    TextBox searchBox;
    @UiField
    Button searchButton;
    @UiField
    TabLayoutPanel repositoryTabPanel;
    @UiField
    ListBox repositoryList;
    @UiField
    ListBox repositoryGroupList;
    @UiField
    FlowPanel resultView;
    /** Saves the current search results **/
    List<SearchResultDto> searchResults = new LinkedList<SearchResultDto>();

    public SearchInterface() {
        resultTable.addColumn(new TextColumn<SearchResultDto>() {

            @Override
            public String getValue(SearchResultDto dto) {
                return String.valueOf(dto.getRelevance());
            }
        }, "Relevance");

        resultTable.addColumn(new TextColumn<SearchResultDto>() {

            @Override
            public String getValue(SearchResultDto dto) {
                return dto.getFilePath();
            }
        }, "Path");

        resultTable.addColumn(new TextColumn<SearchResultDto>() {

            @Override
            public String getValue(SearchResultDto dto) {
                return dto.getRepository();
            }
        }, "Repository");

        initWidget(uiBinder.createAndBindUi(this));
        repositoryTabPanel.selectTab(0);
        resultTablePager.setDisplay(resultTable);
        setResultViewVisible(false);
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
        searchResults.clear();
        String query = searchBox.getText();
        try {
            searcherServiceAsync.doSearch(query, new AsyncCallback<List<SearchResultDto>>() {
                @Override
                public void onFailure(Throwable caught) {
                    Window.alert("Exception calling the search service on the server:\n" + caught);
                }

                @Override
                public void onSuccess(List<SearchResultDto> resultList) {
                    searchResults.addAll(resultList);
                }
            });
        } catch (InvalidIndexLocationException ex) {
            Window.alert("Invalid Index Location");
        }
        for (int i = 0; i < 30; i++) {
            SearchResultDto result = new SearchResultDto();
            result.setFilePath("/aoeu/test");
            result.setRelevance(45.55f);
            result.setRepository("repo1");
            searchResults.add(result);
        }
        setResultViewVisible(true);
        updateResultsView();
    }

    private void updateResultsView() {
        resultTable.setRowData(0, searchResults);
        resultTable.redraw();
    }

     private void setResultViewVisible(boolean visible) {
        resultView.setVisible(visible);
    }
}
