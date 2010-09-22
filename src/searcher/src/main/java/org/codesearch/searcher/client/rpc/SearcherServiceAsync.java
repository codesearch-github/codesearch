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

package org.codesearch.searcher.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.List;
import org.codesearch.searcher.server.InvalidIndexLocationException;
import org.codesearch.searcher.shared.SearchResultDto;

/**
 * Asynchronous Service interface for the searcher.
 * @author Samuel Kogler
 */
public interface SearcherServiceAsync {
    public void doSearch(String query, AsyncCallback<List<SearchResultDto>> asyncCallback) throws InvalidIndexLocationException;
}
