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

import org.codesearch.searcher.shared.SearchType;
import java.util.List;

import org.codesearch.searcher.shared.SearchResultDto;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import java.util.Set;

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
    void setSearchType(SearchType searchType);
    void setSelection(Set<String> selection);
    void setAvailableRepositories(List<String> repositories);
    void setAvailableRepositoryGroups(List<String> repositoryGroups);
    void cleanup();
    ListBox getRepositoryList();
    ListBox getRepositoryGroupList();
    HasValue<Boolean> getCaseSensitive();
    HasValue<String> getSearchBox();
    SearchType getSearchType();
    Set<String> getSelection();

    public interface Presenter {
        void goTo(Place place);
        void doSearch();
    }
}
