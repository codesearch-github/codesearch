package org.codesearch.searcher.client.ui.fileview;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/**
 * The place token used for the file view.
 * @author Samuel Kogler
 */
public class FilePlace extends Place {

    String repository;
    String filePath;

    public FilePlace(String repository, String filePath) {
        this.repository = repository;
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getRepository() {
        return repository;
    }

    public static class Tokenizer implements PlaceTokenizer<FilePlace> {

        @Override
        public String getToken(FilePlace place) {
            return place.getRepository() + "@" + place.getFilePath();
        }

        @Override
        public FilePlace getPlace(String token) {
            String[] parts = token.split("@");
            if (parts.length == 2) {
                return new FilePlace(parts[0], parts[1]);
            } else {
                return null;
            }
        }
    }
}
