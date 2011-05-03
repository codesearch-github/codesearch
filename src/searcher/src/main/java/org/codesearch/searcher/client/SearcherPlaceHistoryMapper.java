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

package org.codesearch.searcher.client;

import org.codesearch.searcher.client.ui.fileview.FilePlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import org.codesearch.searcher.client.ui.searchview.SearchPlace;

/**
 * Manages history tokens throughout the searcher.
 * Tokens only appear in URL if their Tokenizers are annotated here.
 * @author Samuel Kogler
 */
@WithTokenizers({SearchPlace.Tokenizer.class, FilePlace.Tokenizer.class})
public interface SearcherPlaceHistoryMapper extends PlaceHistoryMapper {
}
