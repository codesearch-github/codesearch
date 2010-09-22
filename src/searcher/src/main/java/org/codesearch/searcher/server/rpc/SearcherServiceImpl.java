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

package org.codesearch.searcher.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;

import org.codesearch.searcher.client.rpc.SearcherService;
import org.codesearch.searcher.server.DocumentSearcher;
import org.codesearch.searcher.server.InvalidIndexLocationException;
import org.codesearch.searcher.shared.SearchResultDto;

/**
 * Service used for search operations.
 * @author Samuel Kogler
 */
public class SearcherServiceImpl extends RemoteServiceServlet implements SearcherService {
    /**
     * The document searcher used to search the index.
     */
    private DocumentSearcher documentSearcher;
     /** The logger. */
    private static final Logger LOG = Logger.getLogger(SearcherServiceImpl.class);

    @Override
    public List<SearchResultDto> doSearch(String query) throws InvalidIndexLocationException {
        List<SearchResultDto> resultItems = new LinkedList<SearchResultDto>();
        try {
            resultItems = documentSearcher.search(query);
        } catch (ParseException ex) {
            LOG.error("Could not parse query: " + ex);
        } catch (IOException ex) {
            LOG.error(ex);
        }
        return resultItems;
    }

    public DocumentSearcher getDocumentSearcher() {
        return documentSearcher;
    }

    public void setDocumentSearcher(DocumentSearcher documentSearcher) {
        this.documentSearcher = documentSearcher;
    }
}
