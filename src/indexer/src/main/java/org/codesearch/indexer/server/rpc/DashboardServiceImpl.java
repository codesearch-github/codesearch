/**
 * 
 */
package org.codesearch.indexer.server.rpc;

import java.util.List;

import org.codesearch.indexer.client.rpc.DashboardService;
import org.codesearch.indexer.server.manager.IndexingManager;
import org.codesearch.indexer.shared.DashboardData;
import org.codesearch.indexer.shared.DashboardServiceException;
import org.codesearch.indexer.shared.JobStatus;
import org.quartz.SchedulerException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Samuel Kogler
 *
 */
@Singleton
public class DashboardServiceImpl extends RemoteServiceServlet implements DashboardService {
	
	/** . */
    private static final long serialVersionUID = 8656390076402009245L;
    /** The indexing manager used to query the job status. */
	private IndexingManager indexingManager;

	@Inject
	public DashboardServiceImpl(IndexingManager indexingManager)
	{
		this.indexingManager = indexingManager;
	}
	
	/** {@inheritDoc} */
	@Override
	public DashboardData getData() throws DashboardServiceException {
		try {
			DashboardData dashboardData = new DashboardData();
			
			List<JobStatus> runningJobs = indexingManager.getRunningJobs();
			List<JobStatus> scheduledJobs = indexingManager.getScheduledJobs();
			
			dashboardData.setRunningJobs(runningJobs);
			dashboardData.setScheduledJobs(scheduledJobs);
			
			return dashboardData;
		} catch (SchedulerException e) {
			throw new DashboardServiceException(e.toString());
		}
	}
}
