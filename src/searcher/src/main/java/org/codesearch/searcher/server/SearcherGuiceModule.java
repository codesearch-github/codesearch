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

package org.codesearch.searcher.server;

import org.codesearch.searcher.server.rpc.SearcherServiceImpl;
import org.codesearch.searcher.server.servlets.OpenSearchServlet;
import org.codesearch.searcher.server.servlets.UpdateIndexerServlet;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

/**
 *
 * @author Samuel Kogler
 */
public class SearcherGuiceModule extends ServletModule {

    @Override
    protected void configureServlets() {
        bind(DocumentSearcher.class).to(DocumentSearcherImpl.class).in(Singleton.class);
        serve("/gwt.rpc").with(SearcherServiceImpl.class);
        serve("/refresh").with(UpdateIndexerServlet.class);
        serve("/codesearch-opensearch.xml").with(OpenSearchServlet.class);
    }
}
