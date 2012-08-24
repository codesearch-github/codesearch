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

import org.codesearch.searcher.client.ui.RootContainer;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.web.bindery.event.shared.EventBus;

/**
 * The entry point for the searcher.
 * @author Samuel Kogler
 */
public class SearcherEntryPoint implements EntryPoint {

    private RootContainer rootContainer;

    /** {@inheritDoc} */
    @Override
    public void onModuleLoad() {
        Resources.INSTANCE.searcherStyle().ensureInjected();

        ClientFactory clientFactory = ClientFactory.getDefaultFactory();
        rootContainer = new RootContainer(clientFactory.getDefaultPlace(), clientFactory.getPlaceController());
        EventBus eventBus = clientFactory.getEventBus();
        PlaceController placeController = clientFactory.getPlaceController();

        ActivityMapper activityMapper = new SearcherActivityMapper(clientFactory);
        ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
        activityManager.setDisplay(rootContainer);

        PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(clientFactory.getHistoryMapper());
        historyHandler.register(placeController, eventBus, clientFactory.getDefaultPlace());

        RootLayoutPanel.get().add(rootContainer);
        historyHandler.handleCurrentHistory();
        
        GWT.setUncaughtExceptionHandler(new DefaultExceptionHandler());
    }
}
