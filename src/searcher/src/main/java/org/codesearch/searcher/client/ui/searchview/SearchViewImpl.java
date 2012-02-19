/**
 * Copyright 2010 David Froehlich <david.froehlich@businesssoftware.at>, Samuel
 * Kogler <samuel.kogler@gmail.com>, Stephan Stiboller <stistc06@htlkaindorf.at>
 *
 * This file is part of Codesearch.
 *
 * Codesearch is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Codesearch is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Codesearch. If not, see <http://www.gnu.org/licenses/>.
 */
package org.codesearch.searcher.client.ui.searchview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
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
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.codesearch.searcher.client.ui.fileview.FilePlace;
import org.codesearch.searcher.shared.SearchField;
import org.codesearch.searcher.shared.SearchResultDto;
import org.codesearch.searcher.shared.SearchType;

/**
 * Implements the functionality of the search page. Composite class
 * corresponding to the UiBinder template.
 *
 * @author Samuel Kogler
 */
public class SearchViewImpl extends Composite implements SearchView {

    // CONSTANTS
    private static final int PAGE_SIZE = 200;
    private static final String RELEVANCE_TITLE = "Relevance";
    private static final String PATH_TITLE = "Path";
    private static final String REPOSITORY_TITLE = "Repository";
    private static final String REVISION_TITLE = "Last Revision";

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
    Label helpLabel;
    @UiField
    Button searchButton;
    @UiField
    ListBox maxResults;
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
    @UiField
    DisclosurePanel filterPanel;
    @UiField
    Panel repositoryFilterPanel;
    @UiField
    Panel fileEndingFilterPanel;
    @UiField
    Label resultStatusLabel;
    @UiField
    Button resetAllFiltersButton = new Button("Reset all filters");
    @UiField
    Panel helpBox;
    @UiField
    HTML searchFieldsBox;
    private Presenter presenter;
    private SearchType searchType;
    private NumberFormat relevanceFormatter = NumberFormat.getFormat("00.00");
    private List<SearchResultDto> unfilteredResults;
    private boolean filtersInitialized;
    private boolean repositoriesInitialized;
    private boolean repositoryGroupsInitialized;
    private String searchFieldTooltip = "";

    public SearchViewImpl() {
        initResultTable();
        initWidget(uiBinder.createAndBindUi(this));
        maxResults.setSelectedIndex(1);
        repositoryTabPanel.selectTab(0);
        resultView.setVisible(false);
        repositoryList.clear();
        repositoryGroupList.clear();
        helpBox.setVisible(false);
    }

    @UiHandler("searchButton")
    void onSearchButton(ClickEvent e) {
        presenter.doSearch();
    }

    @UiHandler("searchBox")
    void onSearchBoxKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            event.preventDefault();
            searchButton.click();
        }
    }

    @UiHandler("repositoryTabPanel")
    void onSearchTypeSelection(SelectionEvent<Integer> e) {
        int i = repositoryTabPanel.getSelectedIndex();
        if (i == 0) {
            searchType = SearchType.REPOSITORIES;
        } else if (i == 1) {
            searchType = SearchType.REPOSITORY_GROUPS;
        }
    }

    /**
     * Parses the search results and creates the filter buttons when expanding
     * the filters panel.
     *
     * @param e
     */
    @UiHandler("filterPanel")
    void onFiltersOpen(OpenEvent<DisclosurePanel> e) {
        if (!filtersInitialized) {
            unfilteredResults = new ArrayList<SearchResultDto>(searchResultDataProvider.getList());

            Set<String> fileEndings = new HashSet<String>();
            Set<String> repos = new HashSet<String>();

            for (SearchResultDto result : unfilteredResults) {
                int pos = result.getFilePath().lastIndexOf('.');
                if (pos != -1) {
                    fileEndings.add(result.getFilePath().substring(pos + 1).toLowerCase());
                }
                repos.add(result.getRepository());
            }

            ToggleButton bt = null;
            for (String fileEnding : fileEndings) {
                bt = new ToggleButton(fileEnding);
                bt.addValueChangeHandler(new FilterHandler());
                fileEndingFilterPanel.add(bt);
            }
            for (String repo : repos) {
                bt = new ToggleButton(repo);
                bt.addValueChangeHandler(new FilterHandler());
                repositoryFilterPanel.add(bt);
            }

            filtersInitialized = true;
        }
    }

    @UiHandler("resetAllFiltersButton")
    void onResetFilters(ClickEvent e) {
        for (Widget w : fileEndingFilterPanel) {
            ((ToggleButton) w).setDown(false);
        }
        for (Widget w : repositoryFilterPanel) {
            ((ToggleButton) w).setDown(false);
        }
        searchResultDataProvider.setList(new ArrayList<SearchResultDto>(unfilteredResults));
    }

    @UiHandler("helpLabel")
    void onMouseOverHelp(MouseOverEvent e) {
        searchFieldsBox.setHTML(searchFieldTooltip);
        helpBox.setVisible(true);
    }

    @UiHandler("helpLabel")
    void onMouseOutOfHelp(MouseOutEvent e) {
        helpBox.setVisible(false);
    }

    @Override
    public void cleanup() {
        repositoryList.setSelectedIndex(-1);
        repositoryGroupList.setSelectedIndex(-1);
        searchResultDataProvider.getList().clear();
        resultTable.redraw();
        resultView.setVisible(false);
        resultStatusLabel.setVisible(false);
        searchBox.setValue("");
        resultStatusLabel.setText("");
        setSearchType(SearchType.REPOSITORIES);
        fileEndingFilterPanel.clear();
        repositoryFilterPanel.clear();
        filterPanel.setOpen(false);
        helpBox.setVisible(false);
    }

    @Override
    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
        if (searchType == SearchType.REPOSITORIES) {
            repositoryTabPanel.selectTab(0);
        } else if (searchType == SearchType.REPOSITORY_GROUPS) {
            repositoryTabPanel.selectTab(1);
        }
    }

    @Override
    public Set<String> getSelection() {
        if (searchType == SearchType.REPOSITORIES) {
            return getListSelection(repositoryList);
        } else if (searchType == SearchType.REPOSITORY_GROUPS) {
            return getListSelection(repositoryGroupList);
        } else {
            return null;
        }
    }

    @Override
    public void setSelection(Set<String> selection) {
        if (searchType == SearchType.REPOSITORIES) {
            updateListSelection(repositoryList, selection);
        } else if (searchType == SearchType.REPOSITORY_GROUPS) {
            updateListSelection(repositoryGroupList, selection);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSearchResults(List<SearchResultDto> results) {
        filtersInitialized = false;
        searchResultDataProvider.setList(results);
    }

    @Override
    public void setResultStatusMessage(String message) {
        if (!resultStatusLabel.isVisible()) {
            resultStatusLabel.setVisible(true);
        }
        resultStatusLabel.setText(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAvailableRepositories(List<String> repositories) {
        repositoriesInitialized = true;
        repositoryList.clear();
        for (String repo : repositories) {
            repositoryList.addItem(repo);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAvailableRepositoryGroups(List<String> repositoryGroups) {
        repositoryGroupsInitialized = true;
        repositoryGroupList.clear();
        for (String repo : repositoryGroups) {
            repositoryGroupList.addItem(repo);
        }
    }

    @Override
    public void setSearchFields(List<SearchField> searchFields) {
        for (SearchField searchField : searchFields) {
            searchFieldTooltip += searchField.getName() + ": " + searchField.getDescription();
            searchFieldTooltip += "</br>";
        }
        searchFieldTooltip = searchFieldTooltip.substring(0, searchFieldTooltip.length() - 5);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxResults(int maxResults) {
        for (int i = 0; i < this.maxResults.getItemCount(); i++) {
            int value = Integer.parseInt(this.maxResults.getItemText(i));
            if (value == maxResults) {
                this.maxResults.setSelectedIndex(i);
                return;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HasValue<Boolean> getCaseSensitive() {
        return caseSensitive;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListBox getRepositoryList() {
        return repositoryList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListBox getRepositoryGroupList() {
        return repositoryGroupList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HasValue<String> getSearchBox() {
        return searchBox;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Panel getResultsView() {
        return resultView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchType getSearchType() {
        return searchType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxResults() {
        return Integer.parseInt(maxResults.getItemText(maxResults.getSelectedIndex()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInitialized() {
        return repositoriesInitialized && repositoryGroupsInitialized;
    }

    private Set<String> getListSelection(ListBox lb) {
        Set<String> selection = new HashSet<String>();
        for (int i = 0; i < lb.getItemCount(); i++) {
            if (lb.isItemSelected(i)) {
                selection.add(lb.getItemText(i));
            }
        }
        return selection;
    }

    private void updateListSelection(ListBox lb, Set<String> selection) {
        for (int i = 0; i < lb.getItemCount(); i++) {
            if (selection.contains(lb.getItemText(i))) {
                lb.setItemSelected(i, true);
            } else {
                lb.setItemSelected(i, false);
            }
        }
    }

    private void initResultTable() {
        resultTable = new CellTable<SearchResultDto>(PAGE_SIZE);
        resultTable.addColumn(new TextColumn<SearchResultDto>() {

            /**
             * {@inheritDoc}
             */
            @Override
            public String getValue(SearchResultDto dto) {
                return relevanceFormatter.format(dto.getRelevance());
            }
        }, RELEVANCE_TITLE);

        resultTable.addColumn(new TextColumn<SearchResultDto>() {

            /**
             * {@inheritDoc}
             */
            @Override
            public String getValue(SearchResultDto object) {
                return object.getFilePath();
            }
        }, PATH_TITLE);
        resultTable.addColumn(new TextColumn<SearchResultDto>() {

            /**
             * {@inheritDoc}
             */
            @Override
            public String getValue(SearchResultDto dto) {
                return dto.getRepository();
            }
        }, REPOSITORY_TITLE);

        resultTable.addColumn(new TextColumn<SearchResultDto>() {

            /**
             * {@inheritDoc}
             */
            @Override
            public String getValue(SearchResultDto dto) {
                return dto.getRevision();
            }
        }, REVISION_TITLE);

        final NoSelectionModel<SearchResultDto> selectionModel = new NoSelectionModel<SearchResultDto>();
        resultTable.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

            /**
             * {@inheritDoc}
             */
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

    private class FilterHandler implements ValueChangeHandler<Boolean> {

        public FilterHandler() {
        }

        @Override
        public void onValueChange(ValueChangeEvent<Boolean> event) {
            Set<String> fileEndingFilters = new HashSet<String>();
            Set<String> repoFilters = new HashSet<String>();

            Iterator<Widget> iter = fileEndingFilterPanel.iterator();
            while (iter.hasNext()) {
                ToggleButton bt = (ToggleButton) iter.next();
                if (bt.isDown()) {
                    fileEndingFilters.add(bt.getText());
                }
            }

            iter = repositoryFilterPanel.iterator();
            while (iter.hasNext()) {
                ToggleButton bt = (ToggleButton) iter.next();
                if (bt.isDown()) {
                    repoFilters.add(bt.getText());
                }
            }

            List<SearchResultDto> results = new ArrayList<SearchResultDto>(unfilteredResults);

            for (int i = (results.size() - 1); i >= 0; i--) {
                SearchResultDto result = results.get(i);
                if (!fileEndingFilters.isEmpty()) {
                    int pos = result.getFilePath().lastIndexOf('.');
                    if (pos != -1) {
                        String resultFileEnding = result.getFilePath().substring(pos + 1);
                        if (!fileEndingFilters.contains(resultFileEnding.toLowerCase())) {
                            results.remove(i);
                        }
                    } else {
                        results.remove(i);
                    }
                }

                if (!repoFilters.isEmpty() && !repoFilters.contains(result.getRepository())) {
                    results.remove(i);
                }
            }

            searchResultDataProvider.setList(results);
        }
    }
}
