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

/**
 * The place token used for the file view.
 * Files are uniquely identified by a file path and a repository name.
 * 
 * @author Samuel Kogler
 */
public class FilePlace extends Place {

    private String repository;
    private String filePath;
    private int focusLine;

    public FilePlace(String repository, String filePath, int focusLine) {
        this.repository = repository;
        this.filePath = filePath;
        this.focusLine = focusLine;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getRepository() {
        return repository;
    }

    public int getFocusLine() {
        return focusLine;
    }

    public static class Tokenizer implements PlaceTokenizer<FilePlace> {

        /** {@inheritDoc} */
        @Override
        public String getToken(FilePlace place) {
            return place.focusLine + "@" + place.getFilePath() + "@" + place.getRepository();
        }

        /** {@inheritDoc} */
        @Override
        public FilePlace getPlace(String token) {
            String[] parts = token.split("@");
            if (parts.length == 3) {
                try {
                    int line = Integer.parseInt(parts[0]);
                    return new FilePlace(parts[2], parts[1], line);
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
            return null;
        }
    }
}
