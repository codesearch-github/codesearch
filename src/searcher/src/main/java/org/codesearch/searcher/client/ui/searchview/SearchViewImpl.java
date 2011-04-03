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
package org.codesearch.searcher.client.ui.searchview;

import java.util.List;

import org.codesearch.searcher.client.ui.fileview.FilePlace;
import org.codesearch.searcher.shared.SearchResultDto;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;

/**
 * Implements the functionality of the search page.
 * Composite class corresponding to the UiBinder template.
 * @author Samuel Kogler
 */
public class SearchViewImpl extends Composite implements SearchView {

    // CONSTANTS
    private static final int PAGE_SIZE = 200;
    private static final String RELEVANCE_TITLE = "Relevance";
    private static final String PATH_TITLE = "Path";
    private static final String REPOSITORY_TITLE = "Repository";

    // UIBINDER STUFF
    @UiTemplate("SearchView.ui.xml")
    interface SearchViewUiBinder extends UiBinder<Widget, SearchViewImpl> {
    }
    private static SearchViewUiBinder uiBinder = GWT.create(SearchViewUiBinder.class);
    // RESULT LIST RELATED
    private ListDataProvider<SearchResultDto> searchResultDataProvider;
    @UiField(provided = true)
    CellTable<SearchResultDto> resultTable;
    @UiField(provided = true)
    SimplePager resultTablePager;

    // OTHER UI ELEMENTS
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
    @UiField
    HasValue<Boolean> caseSensitive;
    
    private Presenter presenter;

    public SearchViewImpl() {
        initResultTable();
        initWidget(uiBinder.createAndBindUi(this));
        repositoryTabPanel.selectTab(0);
        updateRepositoryDisplay();
        resultView.setVisible(true);
    }

    @UiHandler("searchButton")
    void onSearchButton(ClickEvent e) {
        presenter.doSearch();
    }

    @UiHandler("searchBox")
    void onSearchBoxKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            searchButton.click();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    /** {@inheritDoc} */
    @Override
    public void setSearchResults(List<SearchResultDto> results) {
        searchResultDataProvider.setList(results);
    }

    /** {@inheritDoc} */
    @Override
    public void setAvailableRepositories(List<String> repositories) {
        repositoryList.clear();
        for (String repo : repositories) {
            repositoryList.addItem(repo);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setAvailableRepositoryGroups(List<String> repositoryGroups) {
        repositoryGroupList.clear();
        for (String repo : repositoryGroups) {
            repositoryGroupList.addItem(repo);
        }
    }

    /** {@inheritDoc} */
    @Override
    public HasValue<Boolean> getCaseSensitive() {
        return caseSensitive;
    }

    /** {@inheritDoc} */
    @Override
    public ListBox getRepositoryList() {
        return repositoryList;
    }

    /** {@inheritDoc} */
    @Override
    public ListBox getRepositoryGroupList() {
        return repositoryGroupList;
    }

    /** {@inheritDoc} */
    @Override
    public HasValue<String> getSearchBox() {
        return searchBox;
    }

    /** {@inheritDoc} */
    @Override
    public Panel getResultsView() {
        return resultView;
    }

    /** {@inheritDoc} */
    @Override
    public RepositorySearchType getRepositorySearchType() {
        if (repositoryTabPanel.getSelectedIndex() == 0) {
            return SearchView.RepositorySearchType.REPOSITORY;
        } else {
            return SearchView.RepositorySearchType.REPOSITORY_GROUPS;
        }
    }

    private void updateRepositoryDisplay() {
        repositoryList.clear();
        repositoryGroupList.clear();
    }

    private void initResultTable() {
        resultTable = new CellTable<SearchResultDto>(PAGE_SIZE);
        resultTable.addColumn(new TextColumn<SearchResultDto>() {

            /** {@inheritDoc} */
            @Override
            public String getValue(SearchResultDto dto) {
                return String.valueOf(dto.getRelevance());
            }
        }, RELEVANCE_TITLE);

        resultTable.addColumn(new TextColumn<SearchResultDto>() {

            /** {@inheritDoc} */
            @Override
            public String getValue(SearchResultDto object) {
                return object.getFilePath();
            }
        }, PATH_TITLE);
        resultTable.addColumn(new TextColumn<SearchResultDto>() {

            /** {@inheritDoc} */
            @Override
            public String getValue(SearchResultDto dto) {
                return dto.getRepository();
            }
        }, REPOSITORY_TITLE);

        final NoSelectionModel<SearchResultDto> selectionModel = new NoSelectionModel<SearchResultDto>();
        resultTable.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

            /** {@inheritDoc} */
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                SearchResultDto selected = selectionModel.getLastSelectedObject();
                if (selected != null) {
                    presenter.goTo(new FilePlace(selected.getRepository(), selected.getFilePath(), searchBox.getValue()));
                }
            }
        });

        // Create a pager to control the table.
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        resultTablePager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
        resultTablePager.setDisplay(resultTable);
        searchResultDataProvider = new ListDataProvider<SearchResultDto>();
        searchResultDataProvider.addDataDisplay(resultTable);
    }
}
