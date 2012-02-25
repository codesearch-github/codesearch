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

package org.codesearch.indexer.client.ui.manualIndexing;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.codesearch.indexer.client.ClientFactory;
import org.codesearch.indexer.client.rpc.ManualIndexingService;
import org.codesearch.indexer.client.rpc.ManualIndexingServiceAsync;
import org.codesearch.indexer.client.ui.dashboard.DashboardPlace;
import org.codesearch.indexer.client.ui.manualIndexing.ManualIndexingView.Presenter;
import org.codesearch.indexer.shared.ManualIndexingData;

/**
 * Presenter for the dashboard.
 * @author Samuel Kogler
 */
public class ManualIndexingActivity extends AbstractActivity implements Presenter {



    private ClientFactory clientFactory;
    private ManualIndexingView manualIndexingView;
    private ManualIndexingServiceAsync manualIndexingServiceAsync = GWT.create(ManualIndexingService.class);

    public ManualIndexingActivity(ClientFactory clientFactory, ManualIndexingPlace place) {
        this.clientFactory = clientFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        manualIndexingView = clientFactory.getManualIndexingView();
        manualIndexingView.setPresenter(this);
        panel.setWidget(manualIndexingView.asWidget());
        refresh();
    }

    /** {@inheritDoc} */
    @Override
    public void goTo(Place place) {
        clientFactory.getPlaceController().goTo(place);
    }

    @Override
    public void onStop() {
        manualIndexingView.disconnectEventHandlers();
        manualIndexingView.cleanup();
    }

    @Override
    public void refresh() {
    	manualIndexingView.cleanup();
    	manualIndexingServiceAsync.getData(new GetDataCallback());
    }

    @Override
    public void startManualIndexing() {
        manualIndexingServiceAsync.startManualIndexing(manualIndexingView.getRepositories(), manualIndexingView.getRepositoryGroups(), manualIndexingView.getClear().getValue(), new ManualIndexingCallback());
    }

    private class ManualIndexingCallback implements AsyncCallback<Void>{

        @Override
        public void onFailure(Throwable caught) {
            Window.alert("Starting the manual indexing job failed, see server log for detailed information");
        }

        @Override
        public void onSuccess(Void result) {
            goTo(new DashboardPlace());
        }

    }

    private class GetDataCallback implements AsyncCallback<ManualIndexingData> {

        public GetDataCallback() {
        }

        @Override
        public void onFailure(Throwable caught) {
            Window.alert("FAIL!!!");//FIXME
        }

        @Override
        public void onSuccess(ManualIndexingData result) {
            manualIndexingView.setRepositories(result.getRepositories());
            manualIndexingView.setRepositoryGroups(result.getRepositoryGroups());
        }
    }
}
