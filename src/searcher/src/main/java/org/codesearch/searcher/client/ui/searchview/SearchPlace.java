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

    public SearchPlace(String searchTerm, SearchType searchType, Set<String> selection) {
        this.searchTerm = searchTerm;
        this.searchType = searchType;
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

    public static class Tokenizer implements PlaceTokenizer<SearchPlace> {

        /** {@inheritDoc} */
        @Override
        public String getToken(SearchPlace place) {
            StringBuilder sb = new StringBuilder();

            sb.append(place.getSearchTerm());
            sb.append(UIConstants.URL_TOKEN_SEPARATOR);

            sb.append(place.getSearchType().toString());
            sb.append(UIConstants.URL_TOKEN_SEPARATOR);

            for (String string : place.getSelection()) {
                sb.append(string);
                sb.append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }

        /** {@inheritDoc} */
        @Override
        public SearchPlace getPlace(String token) {
            try {
                String searchTerm;
                SearchType searchType;
                Set<String> selection;

                String[] tokens = token.split(UIConstants.URL_TOKEN_SEPARATOR);
                if (tokens.length == 3) {
                    searchTerm = tokens[0];
                    searchType = SearchType.valueOf(tokens[1]);
                    selection = new HashSet<String>();
                    if (tokens[2] != null) {
                        selection.addAll(Arrays.asList(tokens[2].split(",")));
                    }
                    return new SearchPlace(searchTerm, searchType, selection);
                }

            } catch (IllegalArgumentException ex) {
            }
            return null;
        }
    }
}
