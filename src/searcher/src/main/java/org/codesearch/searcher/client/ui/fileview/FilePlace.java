package org.codesearch.searcher.client.ui.fileview;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/**
 * The place token used for the file view.
 * @author Samuel Kogler
 */
public class FilePlace extends Place {

    public static class Tokenizer implements PlaceTokenizer<FilePlace> {

        @Override
        public String getToken(FilePlace place) {
            return "lol";
        }

        @Override
        public FilePlace getPlace(String token) {
            return new FilePlace();
        }
    }
}
