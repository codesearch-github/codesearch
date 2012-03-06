/**
 * Copyright 2010 David Froehlich <david.froehlich@businesssoftware.at>, Samuel
 * Kogler <samuel.kogler@gmail.com>, Stephan Stiboller <stistc06@htlkaindorf.at>
 *
 * This file is part of Codesearch.
 *
 * Codesearch is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Codesearch is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Codesearch. If not, see <http://www.gnu.org/licenses/>.
 */
package org.codesearch.searcher.client.ui.fileview;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import org.codesearch.searcher.client.ui.UIConstants;
import org.codesearch.searcher.client.ui.UIUtils;

/**
 * The place token used for the file view. Files are uniquely identified by a
 * file path and a repository name.
 *
 * @author Samuel Kogler
 */
public class FilePlace extends Place {

    private String repository;
    private String filePath;
    private String searchTerm;
    private int lineNumber;

    public FilePlace(String repository, String filePath, String searchTerm, int lineNumber) {
        this.repository = repository;
        this.filePath = filePath;
        this.searchTerm = searchTerm;
        this.lineNumber = lineNumber;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getRepository() {
        return repository;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @Prefix("file")
    public static class Tokenizer implements PlaceTokenizer<FilePlace> {

        /**
         * {@inheritDoc}
         */
        @Override
        public String getToken(FilePlace place) {

            StringBuilder sb = new StringBuilder();

            sb.append("path=");
            sb.append(UIUtils.escape(place.getFilePath()));

            sb.append(UIConstants.URL_TOKEN_SEPARATOR);
            sb.append("repository=");
            sb.append(UIUtils.escape(place.getRepository()));

            if (place.getSearchTerm() != null && !place.getSearchTerm().isEmpty()) {
                sb.append(UIConstants.URL_TOKEN_SEPARATOR);
                sb.append("term=");
                sb.append(UIUtils.escape(place.getSearchTerm()));
            }
            if (place.getLineNumber() != 1) {
                sb.append(UIConstants.URL_TOKEN_SEPARATOR);
                sb.append("line=");
                sb.append(place.getLineNumber());
            }

            return sb.toString();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FilePlace getPlace(String token) {

            String[] parts = token.split(UIConstants.URL_TOKEN_SEPARATOR);
            if (parts.length >= 2) { // path and repo are required
                String path = "";
                String repository = "";
                String term = "";
                int lineNumber = 1;

                for (String t : parts) {
                    if (t.indexOf('=') == -1) {
                        return null;
                    }
                    String value = UIUtils.unescape(t.substring(t.indexOf('=') + 1));

                    if (t.startsWith("repository=")) {
                        repository = value;
                    } else if (t.startsWith("path=")) {
                        path = value;
                    } else if (t.startsWith("term=")) {
                        term = value;
                    } else if (t.startsWith("line=")) {
                        try {
                            lineNumber = Integer.parseInt(value);
                        } catch (NumberFormatException ex) {
                        }
                    }
                }

                if (path.isEmpty() || repository.isEmpty()) {
                    return null;
                }

                return new FilePlace(repository, path, term, lineNumber);
            }
            return null;
        }
    }
}
