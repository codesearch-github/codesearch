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

import com.google.gwt.http.client.URL;
import org.codesearch.searcher.shared.SearchType;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.codesearch.searcher.client.ui.UIConstants;

/**
 * The place token used for the search view.
 * @author Samuel Kogler
 */
public class SearchPlace extends Place {

    private String searchTerm;
    private Set<String> selection;
    private SearchType searchType;
    private int maxResults;

    public SearchPlace(String searchTerm, SearchType searchType, Set<String> selection, int maxResults) {
        this.searchTerm = searchTerm;
        this.searchType = searchType;
        this.maxResults = maxResults;
        if (selection == null) {
            this.selection = new HashSet<String>();
        } else {
            this.selection = selection;
        }
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public Set<String> getSelection() {
        return selection;
    }

    public void setSelection(Set<String> selection) {
        this.selection = selection;
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public static class Tokenizer implements PlaceTokenizer<SearchPlace> {

        /** {@inheritDoc} */
        @Override
        public String getToken(SearchPlace place) {
            StringBuilder sb = new StringBuilder();

            sb.append("term=");
            sb.append(escape(place.getSearchTerm()));

            sb.append(UIConstants.URL_TOKEN_SEPARATOR);

            sb.append("searchType=");
            sb.append(escape(place.getSearchType().toString()));

            sb.append(UIConstants.URL_TOKEN_SEPARATOR);

            sb.append("selection=");
            if (place.getSelection() != null && !place.getSelection().isEmpty()) {
                for (String string : place.getSelection()) {
                    sb.append(escape(string));
                    sb.append(',');
                }
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append(UIConstants.URL_TOKEN_SEPARATOR);
            sb.append("maxResults=");
            sb.append(place.getMaxResults());
            return sb.toString();
        }

        private String escape(String term) {
            term = term.replaceAll(UIConstants.URL_TOKEN_SEPARATOR, "%40");
            term = term.replaceAll(",", "%2C");
            return term;
        }

        private String unescape(String term) {
            term = term.replaceAll("%40", UIConstants.URL_TOKEN_SEPARATOR);
            term = term.replaceAll("%2C", ",");
            return term;
        }

        /** {@inheritDoc} */
        @Override
        public SearchPlace getPlace(String token) {
            try {
                String searchTerm = "";
                SearchType searchType = SearchType.REPOSITORIES;
                Set<String> selection = new HashSet<String>();
                int maxResults = 200;

                String[] tokens = token.split(UIConstants.URL_TOKEN_SEPARATOR);
                if (tokens.length == 4) {
                    for (String t : tokens) {
                        if (t.indexOf('=') == -1) {
                            return null;
                        }
                        String value = unescape(t.substring(t.indexOf('=') + 1));
                        if (t.startsWith("term=")) {
                            searchTerm = value;
                        } else if (t.startsWith("searchType=")) {
                            searchType = SearchType.valueOf(value);
                        } else if (t.startsWith("selection=")) {
                            if (value != null && !value.isEmpty()) {
                                for(String v : value.split(",")) {
                                    selection.add(unescape(v));
                                }
                            }
                        } else if (t.startsWith("maxResults=")) {
                            maxResults = Integer.parseInt(value);
                        }
                    }
                    return new SearchPlace(searchTerm, searchType, selection, maxResults);
                }

            } catch (IllegalArgumentException ex) {
            }
            return null;
        }
    }
}
