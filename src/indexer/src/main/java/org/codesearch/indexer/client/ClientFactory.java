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
package org.codesearch.indexer.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import org.codesearch.indexer.client.ui.dashboard.DashboardView;
import org.codesearch.indexer.client.ui.log.LogView;
import org.codesearch.indexer.client.ui.manualIndexing.ManualIndexingView;

/**
 *
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

    public abstract DashboardView getDashboardView();

    public abstract LogView getLogView();

    public abstract ManualIndexingView getManualIndexingView();
}
