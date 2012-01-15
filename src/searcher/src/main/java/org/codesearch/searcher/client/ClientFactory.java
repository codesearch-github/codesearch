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

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import org.codesearch.searcher.client.ui.fileview.FileView;
import org.codesearch.searcher.client.ui.searchview.SearchView;

/**
 * Provides the implementations of classes used throughout the client.
 * @author Samuel Kogler
 */
public abstract class ClientFactory {

    private static ClientFactory clientFactory;

    public static ClientFactory getDefaultFactory() {
        if (clientFactory == null) {
            clientFactory = new ClientFactoryImpl();
        }
        return clientFactory;
    }

    public abstract EventBus getEventBus();

    public abstract PlaceController getPlaceController();

    public abstract SearchView getSearchView();

    public abstract FileView getFileView();

    public abstract Place getDefaultPlace();
}
