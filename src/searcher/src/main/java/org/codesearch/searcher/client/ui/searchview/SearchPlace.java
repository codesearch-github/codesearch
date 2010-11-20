package org.codesearch.searcher.client.ui.searchview;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/**
 * The place token used for the search view.
 * @author Samuel Kogler
 */
public class SearchPlace extends Place {

    public static class Tokenizer implements PlaceTokenizer<SearchPlace> {

        @Override
        public String getToken(SearchPlace place) {
            return null;
        }

        @Override
        public SearchPlace getPlace(String token) {
            return new SearchPlace();
        }
    }
}
