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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;
import org.codesearch.searcher.client.ui.fileview.FileView;
import org.codesearch.searcher.client.ui.fileview.FileViewImpl;
import org.codesearch.searcher.client.ui.searchview.SearchView;
import org.codesearch.searcher.client.ui.searchview.SearchViewImpl;

/**
 * Implementation of the factory.
 * @author Samuel Kogler
 */
public class ClientFactoryImpl implements ClientFactory {

    private final EventBus eventBus = new SimpleEventBus();
    private final PlaceController placeController = new PlaceController(eventBus);
    private final SearchView searchView = new SearchViewImpl();
    private final FileView fileView = FileViewImpl.getInstance();

    /** {@inheritDoc} */
    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    /** {@inheritDoc} */
    @Override
    public PlaceController getPlaceController() {
        return placeController;
    }

    /** {@inheritDoc} */
    @Override
    public SearchView getSearchView() {
        return searchView;
    }

    /** {@inheritDoc} */
    @Override
    public FileView getFileView() {
        return fileView;
    }
}
