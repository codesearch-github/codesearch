package org.codesearch.searcher.client;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import org.codesearch.searcher.client.ui.fileview.FilePlace;

/**
 * Manages history tokens throughout the searcher.
 * Tokens only appear in URL if their Tokenizers are annotated here.
 * @author Samuel Kogler
 */
//@WithTokenizers({SearchPlace.Tokenizer.class, FilePlace.Tokenizer.class})
@WithTokenizers({FilePlace.Tokenizer.class})
public interface SearcherPlaceHistoryMapper extends PlaceHistoryMapper {
}
