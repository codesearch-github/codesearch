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

import org.codesearch.indexer.shared.JobStatus;
import org.codesearch.indexer.shared.RepositoryStatus;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Displays general information like current status and errors.
 * @author Samuel Kogler
 */
public interface DashboardView extends IsWidget {

    /**
     * Resets the state of the view.
     */
    void cleanup();

    /**
     * Sets the presenter for this view.
     * @param presenter The presenter
     */
    void setPresenter(DashboardView.Presenter presenter);

    /**
     * Connects event handlers for hotkeys.
     */
    void connectEventHandlers();

    /**
     * Disconnects event handlers for hotkeys.
     */
    void disconnectEventHandlers();


    Presenter getPresenter();

    /**
     * Gets the button that should refresh the displayed data.
     * @return The button
     */
    HasClickHandlers getRefreshButton();

    /**
     * Returns the table that is used to display scheduled jobs.
     * @return The table.
     */
    CellTable<JobStatus> getScheduledJobsTable();

    /**
     * Returns the table that is used to display running jobs.
     * @return The table.
     */
    CellTable<JobStatus> getRunningJobsTable();

    /**
     * Returns the table that is used to display the repository statuses
     * @return The table
     */
    CellTable<RepositoryStatus> getRepositoryStatuses();

    interface Presenter {
        void goTo(Place place);
        void refresh();
    }
}
