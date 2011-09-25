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

package org.codesearch.indexer.client.ui.dashboard;

import org.codesearch.indexer.client.ClientFactory;
import org.codesearch.indexer.client.rpc.DashboardService;
import org.codesearch.indexer.client.rpc.DashboardServiceAsync;
import org.codesearch.indexer.client.ui.dashboard.DashboardView.Presenter;
import org.codesearch.indexer.shared.DashboardData;
import org.codesearch.indexer.shared.JobStatus;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.view.client.ListDataProvider;

/**
 * Presenter for the dashboard.
 * @author Samuel Kogler
 */
public class DashboardActivity extends AbstractActivity implements Presenter {

    private ClientFactory clientFactory;
    private DashboardView dashboardView;
    private DashboardServiceAsync dashboardServiceAsync = GWT.create(DashboardService.class);
    
    private ListDataProvider<JobStatus> runningJobsDataProvider = new ListDataProvider<JobStatus>();
    private ListDataProvider<JobStatus> scheduledJobsDataProvider = new ListDataProvider<JobStatus>();
   
    public DashboardActivity(ClientFactory clientFactory, DashboardPlace place) {
        this.clientFactory = clientFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        dashboardView = clientFactory.getDashboardView();
        dashboardView.setPresenter(this);
        panel.setWidget(dashboardView.asWidget());
        
        runningJobsDataProvider.addDataDisplay(dashboardView.getRunningJobsTable());
        scheduledJobsDataProvider.addDataDisplay(dashboardView.getScheduledJobsTable());
        
        refresh();
    }

    /** {@inheritDoc} */
    @Override
    public void goTo(Place place) {
        clientFactory.getPlaceController().goTo(place);
    }

    @Override
    public void onStop() {
        dashboardView.disconnectEventHandlers();
        dashboardView.cleanup();
        runningJobsDataProvider.removeDataDisplay(dashboardView.getRunningJobsTable());
        scheduledJobsDataProvider.removeDataDisplay(dashboardView.getScheduledJobsTable());
    }
    
    @Override
    public void refresh() {
    	dashboardView.cleanup();
    	dashboardServiceAsync.getData(new DashboardDataCallback());
    }
    
    class DashboardDataCallback implements AsyncCallback<DashboardData> {

		/** {@inheritDoc} */
		@Override
		public void onFailure(Throwable caught) {
			Window.alert("Error retrieving data for the dashboard: " + caught);
		}

		/** {@inheritDoc} */
		@Override
		public void onSuccess(DashboardData result) {
			runningJobsDataProvider.setList(result.getRunningJobs());
			scheduledJobsDataProvider.setList(result.getScheduledJobs());
		}
    }
}
