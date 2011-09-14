package org.codesearch.indexer.client.ui.dashboard;

import java.util.Date;

import org.codesearch.indexer.shared.JobStatus;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.DateCell;
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
    @UiField
    HasClickHandlers refreshButton;

    private Presenter presenter;

    public DashboardViewImpl() {
        initTables();
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("refreshButton")
    void onClick(ClickEvent e) {
        presenter.refresh();
    }

    /** {@inheritDoc} */
    @Override
    public void cleanup() {
        
    }

    /** {@inheritDoc} */
    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    /** {@inheritDoc} */
    @Override
    public void connectEventHandlers() {
        // TODO Auto-generated method stub

    }

    /** {@inheritDoc} */
    @Override
    public void disconnectEventHandlers() {
        // TODO Auto-generated method stub

    }

    /** {@inheritDoc} */
    @Override
    public Presenter getPresenter() {
        return presenter;
    }

    /** {@inheritDoc} */
    @Override
    public HasClickHandlers getRefreshButton() {
        return refreshButton;
    }

    /** {@inheritDoc} */
    @Override
    public CellTable<JobStatus> getScheduledJobsTable() {
        return scheduledJobs;
    }

    /** {@inheritDoc} */
    @Override
    public CellTable<JobStatus> getRunningJobsTable() {
        return runningJobs;
    }
    
    
    private void initTables()
	{
        Cell<Date> dateCell = new DateCell(DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_SHORT));
        
	    scheduledJobs = new CellTable<JobStatus>();
	    scheduledJobs.addColumn(new JobStatusNameColumn(), "Name");
	    scheduledJobs.addColumn(new JobStatusNextExecutionColumn(dateCell), "Next execution");
	    scheduledJobs.addColumn(new JobStatusFinishedColumn(dateCell), "Last execution");
	    scheduledJobs.setSelectionModel(new NoSelectionModel<JobStatus>());
	    
	    runningJobs = new CellTable<JobStatus>();
        runningJobs.addColumn(new JobStatusNameColumn(), "Name");
        runningJobs.addColumn(new JobStatusNextExecutionColumn(dateCell), "Started");
        runningJobs.addColumn(new JobStatusCurrentRepoColumn(), "Current repository");
        runningJobs.addColumn(new JobStatusTotalReposColumn(), "Total repositories");
        
        runningJobs.setSelectionModel(new NoSelectionModel<JobStatus>());	    
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
    
    private class JobStatusTotalReposColumn extends TextColumn<JobStatus> {
        @Override
        public String getValue(JobStatus object) {
            return String.valueOf(object.getTotalRepositories());
        }
    }



}
