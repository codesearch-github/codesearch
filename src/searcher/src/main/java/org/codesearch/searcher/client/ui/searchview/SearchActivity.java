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

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codesearch.searcher.client.ClientFactory;
import org.codesearch.searcher.client.rpc.SearcherService;
import org.codesearch.searcher.client.rpc.SearcherServiceAsync;
import org.codesearch.searcher.shared.SearchResultDto;

/**
 * Presenter for the search view.
 * @author Samuel Kogler
 */
public class SearchActivity extends AbstractActivity implements SearchView.Presenter {

    private static final Logger LOG = Logger.getLogger(SearchActivity.class.getName());
    private ClientFactory clientFactory;
    private SearchView searchView;
    private SearcherServiceAsync searcherServiceAsync = GWT.create(SearcherService.class);
    private SearchPlace searchPlace;

    public SearchActivity(ClientFactory clientFactory, SearchPlace searchPlace) {
        this.clientFactory = clientFactory;
        this.searchPlace = searchPlace;
    }

    /** {@inheritDoc} */
    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        searchView = clientFactory.getSearchView();
        searchView.setPresenter(this);
        searchView.setSearchType(searchPlace.getSearchType());
        searchView.getSearchBox().setValue(searchPlace.getSearchTerm());
        searchView.setMaxResults(searchPlace.getMaxResults());
        panel.setWidget(searchView.asWidget());
        if (!searchView.isInitialized()) {
            LOG.log(Level.INFO, "Retrieving repository information.");
            updateRepositories();
        } else {
            searchView.setSelection(searchPlace.getSelection());
            search();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void goTo(Place place) {
        clientFactory.getPlaceController().goTo(place);
    }

    /** {@inheritDoc} */
    @Override
    public void doSearch() {
        goTo(new SearchPlace(searchView.getSearchBox().getValue(), searchView.getSearchType(), searchView.getSelection(), searchView.getMaxResults()));
    }

    private void search() {
        String query = searchView.getSearchBox().getValue();
        if (searchView.isInitialized() && !query.trim().isEmpty()) {
            LOG.log(Level.INFO, "Executing search");

            searcherServiceAsync.doSearch(query, searchView.getCaseSensitive().getValue(),
                    searchView.getSearchType(), searchView.getSelection(), searchView.getMaxResults(), new DoSearchHandler());
        }
    }

    private void updateRepositories() {
        searcherServiceAsync.getAvailableRepositories(new AsyncCallback<List<String>>() {

            /** {@inheritDoc} */
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Remote call trying to fetch available repositories failed:\n" + caught);
            }

            /** {@inheritDoc} */
            @Override
            public void onSuccess(List<String> result) {
                searchView.setAvailableRepositories(result);
                searchView.setSelection(searchPlace.getSelection());
                search();
            }
        });
        searcherServiceAsync.getAvailableRepositoryGroups(new AsyncCallback<List<String>>() {

            /** {@inheritDoc} */
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Remote call trying to fetch available repository groups failed:\n" + caught);
            }

            /** {@inheritDoc} */
            @Override
            public void onSuccess(List<String> result) {
                searchView.setAvailableRepositoryGroups(result);
                searchView.setSelection(searchPlace.getSelection());
                search();
            }
        });
    }

    @Override
    public void onStop() {
        searchView.cleanup();
    }

    /**
     * Handles calls to the doSearch RPC method.
     */
    private class DoSearchHandler implements AsyncCallback<List<SearchResultDto>> {

        /** {@inheritDoc} */
        @Override
        public void onFailure(Throwable caught) {
            Window.alert("Exception calling the search service on the server:\n" + caught);
            searchView.getResultsView().setVisible(false);
        }

        /** {@inheritDoc} */
        @Override
        public void onSuccess(List<SearchResultDto> resultList) {
            if (resultList != null) {
                searchView.setResultStatusMessage(resultList.size() + " results found.");
                if (!resultList.isEmpty()) {
                    searchView.setSearchResults(resultList);
                    searchView.getResultsView().setVisible(true);
                }
            }
        }
    }
}
