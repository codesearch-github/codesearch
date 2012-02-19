package org.codesearch.searcher.shared;

import java.io.Serializable;
import java.util.List;

/**
 * Contains the data that is needed by the SearchView.
 *
 * @author Samuel Kogler
 */
public class SearchViewData implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> repositories;
    private List<String> groups;
    private List<SearchField> searchFields;

    public SearchViewData() {
    }

    public SearchViewData(List<String> repositories, List<String> groups, List<SearchField> searchFields) {
        this.repositories = repositories;
        this.groups = groups;
        this.searchFields = searchFields;
    }

    public List<String> getGroups() {
        return groups;
    }

    public List<String> getRepositories() {
        return repositories;
    }

    public List<SearchField> getSearchFields() {
        return searchFields;
    }
}
