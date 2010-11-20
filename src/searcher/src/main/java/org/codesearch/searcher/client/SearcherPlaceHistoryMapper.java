
package org.codesearch.searcher.client;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import org.codesearch.searcher.client.ui.searchview.SearchPlace;

/**
 * Manages history tokens throughout the searcher.
 * @author Samuel Kogler
 */
@WithTokenizers(SearchPlace.Tokenizer.class)
public interface SearcherPlaceHistoryMapper extends PlaceHistoryMapper { }
