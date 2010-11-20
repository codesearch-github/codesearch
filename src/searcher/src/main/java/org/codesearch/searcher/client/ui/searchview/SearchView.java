
package org.codesearch.searcher.client.ui.searchview;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import java.util.List;
import org.codesearch.searcher.shared.SearchResultDto;

/**
 * View that allows the entering of a search query,
 * selecting of repositories(and groups) to search,
 * and displaying and selecting search results.
 * @author Samuel Kogler
 */
public interface SearchView extends IsWidget {
    void setPresenter(Presenter presenter);
    Panel getResultsView();
    void setSearchResults(List<SearchResultDto> results);
    void setAvailableRepositories(List<String> repositories);
    void setAvailableRepositoryGroups(List<String> repositoryGroups);
    ListBox getRepositoryList();
    ListBox getRepositoryGroupList();
    HasValue<Boolean> getCaseSensitive();
    HasValue<String> getSearchBox();
    RepositorySearchType getRepositorySearchType();

    enum RepositorySearchType {
        REPOSITORY,
        REPOSITORY_GROUPS
    };

    public interface Presenter {
        void goTo(Place place);
        void doSearch();
    }
}
