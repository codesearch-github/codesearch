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

package org.codesearch.indexer.client.ui.log;

import java.util.List;

import org.codesearch.indexer.client.ClientFactory;
import org.codesearch.indexer.client.rpc.DashboardService;
import org.codesearch.indexer.client.rpc.DashboardServiceAsync;
import org.codesearch.indexer.client.rpc.LogService;
import org.codesearch.indexer.client.rpc.LogServiceAsync;
import org.codesearch.indexer.client.ui.log.LogView.Presenter;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * Presenter for the log page.
 * @author Samuel Kogler
 */
public class LogActivity extends AbstractActivity implements Presenter {

    private ClientFactory clientFactory;
    private LogView logView;
    
    private LogServiceAsync logServiceAsync = GWT.create(LogService.class);


    public LogActivity(ClientFactory clientFactory, LogPlace place) {
        this.clientFactory = clientFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        logView = clientFactory.getLogView();
        logView.setPresenter(this);
        panel.setWidget(logView.asWidget());
        refresh();
    }

    /** {@inheritDoc} */
    @Override
    public void goTo(Place place) {
        clientFactory.getPlaceController().goTo(place);
    }

    @Override
    public void onStop() {
        logView.disconnectEventHandlers();
        logView.cleanup();
    }

    /** {@inheritDoc} */
    @Override
    public void refresh() {
        logServiceAsync.getLog(new LogServiceCallback());
    }
    
    private class LogServiceCallback implements AsyncCallback<List<String>>
    {

        /** {@inheritDoc} */
        @Override
        public void onFailure(Throwable caught) {
            Window.alert("Could not retrieve log data: \n" + caught);
        }

        /** {@inheritDoc} */
        @Override
        public void onSuccess(List<String> result) {
            logView.setLog(result);
        }
    }
    
}
