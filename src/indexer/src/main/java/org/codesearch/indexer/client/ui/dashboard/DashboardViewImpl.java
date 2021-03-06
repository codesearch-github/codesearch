/**
 * Copyright 2010 David Froehlich <david.froehlich@businesssoftware.at>, Samuel
 * Kogler <samuel.kogler@gmail.com>, Stephan Stiboller <stistc06@htlkaindorf.at>
 *
 * This file is part of Codesearch.
 *
 * Codesearch is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Codesearch is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Codesearch. If not, see <http://www.gnu.org/licenses/>.
 */
package org.codesearch.indexer.client.ui.dashboard;

import java.util.Date;

import org.codesearch.indexer.shared.JobStatus;
import org.codesearch.indexer.shared.RepositoryStatus;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.NoSelectionModel;

public class DashboardViewImpl extends Composite implements DashboardView {

    private static IndexViewImplUiBinder uiBinder = GWT.create(IndexViewImplUiBinder.class);

    interface IndexViewImplUiBinder extends UiBinder<Widget, DashboardViewImpl> {
    }
    @UiField(provided = true)
    CellTable<JobStatus> runningJobs;
    @UiField(provided = true)
    CellTable<JobStatus> scheduledJobs;
    @UiField(provided = true)
    CellTable<JobStatus> delayedJobs;
    @UiField
    HasClickHandlers refreshButton;
    @UiField(provided = true)
    CellTable<RepositoryStatus> repositoryStatuses; // yeah, it's actually statuses, I own a dictionary
    private Presenter presenter;

    public DashboardViewImpl() {
        initTables();
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("refreshButton")
    void onClick(ClickEvent e) {
        presenter.refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connectEventHandlers() {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnectEventHandlers() {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Presenter getPresenter() {
        return presenter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CellTable<RepositoryStatus> getRepositoryStatuses() {
        return repositoryStatuses;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HasClickHandlers getRefreshButton() {
        return refreshButton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CellTable<JobStatus> getScheduledJobsTable() {
        return scheduledJobs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CellTable<JobStatus> getRunningJobsTable() {
        return runningJobs;
    }

    @Override
    public CellTable<JobStatus> getDelayedJobsTable() {
        return delayedJobs;
    }

    private void initTables() {
        Cell<Date> dateCell = new DateCell(DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_SHORT));

        scheduledJobs = new CellTable<JobStatus>();
        scheduledJobs.addColumn(new JobStatusNameColumn(), "Name");
        scheduledJobs.addColumn(new JobStatusNextExecutionColumn(dateCell), "Next execution");
        scheduledJobs.addColumn(new JobStatusFinishedColumn(dateCell), "Last execution");
        scheduledJobs.setSelectionModel(new NoSelectionModel<JobStatus>());
        scheduledJobs.setPageSize(Integer.MAX_VALUE);

        delayedJobs = new CellTable<JobStatus>();
        delayedJobs.addColumn(new JobStatusNameColumn(), "Name");
        delayedJobs.setSelectionModel(new NoSelectionModel<JobStatus>());
        delayedJobs.setPageSize(Integer.MAX_VALUE);

        runningJobs = new CellTable<JobStatus>();
        runningJobs.addColumn(new JobStatusNameColumn(), "Name");
        runningJobs.addColumn(new JobStatusNextExecutionColumn(dateCell), "Started");
        runningJobs.addColumn(new JobStatusCurrentRepoColumn(), "Current repository");
        runningJobs.addColumn(new JobStatusTotalReposColumn(), "Total repositories");
        runningJobs.addColumn(new JobStatusStepColumn(), "Status");
        runningJobs.addColumn(new JobStatusTotalStepsColumn(), "Total steps");
        runningJobs.addColumn(new JobStatusFinishedStepsColumn(), "Progress");
        runningJobs.setSelectionModel(new NoSelectionModel<JobStatus>());
        runningJobs.setPageSize(Integer.MAX_VALUE);

        repositoryStatuses = new CellTable<RepositoryStatus>();
        repositoryStatuses.addColumn(new RepositoryStatusNameColumn(), "Name");
        repositoryStatuses.addColumn(new RepositoryStatusRevisionColumn(), "Revision");
        repositoryStatuses.addColumn(new RepositoryStatusStatusColumn(), "Status");
        repositoryStatuses.setSelectionModel(new NoSelectionModel<RepositoryStatus>());
        repositoryStatuses.setPageSize(Integer.MAX_VALUE);
    }

    private class RepositoryStatusNameColumn extends TextColumn<RepositoryStatus> {

        @Override
        public String getValue(RepositoryStatus object) {
            return object.getRepositoryName();
        }
    }

    private class RepositoryStatusRevisionColumn extends TextColumn<RepositoryStatus> {

        @Override
        public String getValue(RepositoryStatus object) {
            return object.getRevision();
        }
    }

    private class RepositoryStatusStatusColumn extends TextColumn<RepositoryStatus> {

        @Override
        public String getValue(RepositoryStatus object) {
            String returnValue = "";
            switch (object.getStatus()) {
                case EMPTY:
                    returnValue = "unindexed";
                    break;
                case INCONSISTENT:
                    returnValue = "inconsistent";
                    break;
                case INDEXED:
                    returnValue = "indexed";
                    break;
            }
            return returnValue;
        }
    }

    private class JobStatusNameColumn extends TextColumn<JobStatus> {

        @Override
        public String getValue(JobStatus object) {
            return object.getName();
        }
    }

    private class JobStatusNextExecutionColumn extends Column<JobStatus, Date> {

        /**
         * @param cell
         */
        public JobStatusNextExecutionColumn(Cell<Date> cell) {
            super(cell);
        }

        @Override
        public Date getValue(JobStatus object) {
            return object.getStart();
        }
    }

    private class JobStatusFinishedColumn extends Column<JobStatus, Date> {

        /**
         * @param cell
         */
        public JobStatusFinishedColumn(Cell<Date> cell) {
            super(cell);
        }

        @Override
        public Date getValue(JobStatus object) {
            return object.getFinished();
        }
    }

    private class JobStatusCurrentRepoColumn extends TextColumn<JobStatus> {

        @Override
        public String getValue(JobStatus object) {
            return object.getCurrentRepository();
        }
    }

    private class JobStatusStepColumn extends TextColumn<JobStatus> {

        @Override
        public String getValue(JobStatus object) {
            return object.getCurrentStep().getName();
        }
    }

    private class JobStatusTotalStepsColumn extends Column<JobStatus, Number> {

        public JobStatusTotalStepsColumn() {
            super(new NumberCell());
        }

        @Override
        public Number getValue(JobStatus object) {
            return object.getCurrentStep().getTotalSteps();
        }
    }

    private class JobStatusFinishedStepsColumn extends Column<JobStatus, Number> {

        public JobStatusFinishedStepsColumn() {
            super(new ProgressBarCell(200));
        }

        @Override
        public Number getValue(JobStatus object) {
            float current = object.getCurrentStep().getFinishedSteps();
            float totalSteps = object.getCurrentStep().getTotalSteps();
            return new Float(current / totalSteps);
        }
    }

    private class JobStatusTotalReposColumn extends TextColumn<JobStatus> {

        @Override
        public String getValue(JobStatus object) {
            return String.valueOf(object.getRepositories().size());
        }
    }
}
