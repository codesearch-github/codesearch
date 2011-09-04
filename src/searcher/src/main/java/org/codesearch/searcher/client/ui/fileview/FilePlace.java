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
package org.codesearch.searcher.client.ui.fileview;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import org.codesearch.searcher.client.ui.UIConstants;
import org.codesearch.searcher.client.ui.UIUtils;

/**
 * The place token used for the file view.
 * Files are uniquely identified by a file path and a repository name.
 *
 * @author Samuel Kogler
 */
public class FilePlace extends Place {

    private String repository;
    private String filePath;
    private String searchTerm;

    public FilePlace(String repository, String filePath, String searchTerm) {
        this.repository = repository;
        this.filePath = filePath;
        this.searchTerm = searchTerm;
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

    public static class Tokenizer implements PlaceTokenizer<FilePlace> {

        /** {@inheritDoc} */
        @Override
        public String getToken(FilePlace place) {

            StringBuilder sb = new StringBuilder();

            sb.append("path=");
            sb.append(UIUtils.escape(place.getFilePath()));

            sb.append(UIConstants.URL_TOKEN_SEPARATOR);
            sb.append("repository=");
            sb.append(UIUtils.escape(place.getRepository()));

            sb.append(UIConstants.URL_TOKEN_SEPARATOR);
            sb.append("term=");
            sb.append(UIUtils.escape(place.getSearchTerm()));

            return sb.toString();
        }

        /** {@inheritDoc} */
        @Override
        public FilePlace getPlace(String token) {

            String[] parts = token.split(UIConstants.URL_TOKEN_SEPARATOR);
            if (parts.length >= 2) { // path and repo are required
                String path = "";
                String repository = "";
                String term = "";

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
                    }
                }

                if(path.isEmpty() || repository.isEmpty()) {
                    return null;
                }

                return new FilePlace(repository, path, term);
            }
            return null;
        }
    }
}
