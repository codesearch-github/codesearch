package org.codesearch.searcher.client.ui.searchview;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import java.util.LinkedList;
import java.util.List;
import org.codesearch.searcher.client.ClientFactory;
import org.codesearch.searcher.client.rpc.SearcherService;
import org.codesearch.searcher.client.rpc.SearcherServiceAsync;
import org.codesearch.searcher.shared.SearchResultDto;

/**
 * Presenter for the search view.
 * @author Samuel Kogler
 */
public class SearchActivity extends AbstractActivity implements SearchView.Presenter {

    private ClientFactory clientFactory;
    private SearchView searchView;
    private SearcherServiceAsync searcherServiceAsync = GWT.create(SearcherService.class);

    /**
     * Handles calls to the doSearch RPC method.
     */
    private class DoSearchHandler implements AsyncCallback<List<SearchResultDto>> {

        @Override
        public void onFailure(Throwable caught) {
            Window.alert("Exception calling the search service on the server:\n" + caught);
            searchView.getResultsView().setVisible(false);
        }

        @Override
        public void onSuccess(List<SearchResultDto> resultList) {
            searchView.setSearchResults(resultList);
            searchView.getResultsView().setVisible(true);
        }
    }

    public SearchActivity(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    /**
     * Starts the activity.
     * @param panel
     * @param eventBus
     */
    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        searchView = clientFactory.getSearchView();
        searchView.setPresenter(this);
        updateRepositories();
        panel.setWidget(searchView.asWidget());
    }

    /**
     * Navigate to a new place.
     * @param place
     */
    @Override
    public void goTo(Place place) {
        clientFactory.getPlaceController().goTo(place);
    }

    @Override
    public void doSearch() {
        String query = searchView.getSearchBox().getValue();
        List<String> selectedRepositories = new LinkedList<String>();
        List<String> selectedRepositoryGroups = new LinkedList<String>();
        if (searchView.getRepositorySearchType() == SearchView.RepositorySearchType.REPOSITORY) {
            for (int i = 0; i < searchView.getRepositoryList().getItemCount(); i++) {
                if (searchView.getRepositoryList().isItemSelected(i)) {
                    selectedRepositories.add(searchView.getRepositoryList().getValue(i));
                }
            }
        } else {
            for (int i = 0; i < searchView.getRepositoryList().getItemCount(); i++) {
                if (searchView.getRepositoryList().isItemSelected(i)) {
                    selectedRepositoryGroups.add(searchView.getRepositoryList().getValue(i));
                }
            }
        }
        searcherServiceAsync.doSearch(query, searchView.getCaseSensitive().getValue(),
                selectedRepositories, selectedRepositoryGroups, new DoSearchHandler());
    }

    private void updateRepositories() {
        searcherServiceAsync.getAvailableRepositories(new AsyncCallback<List<String>>() {

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Remote call trying to fetch available repositories failed:\n" + caught);
            }

            @Override
            public void onSuccess(List<String> result) {
                searchView.setAvailableRepositories(result);
            }
        });
        searcherServiceAsync.getAvailableRepositoryGroups(new AsyncCallback<List<String>>() {

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Remote call trying to fetch available repository groups failed:\n" + caught);
            }

            @Override
            public void onSuccess(List<String> result) {
                for (String repo : result) {
                    searchView.setAvailableRepositoryGroups(result);
                }
            }
        });
    }
}
