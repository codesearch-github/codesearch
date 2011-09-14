package org.codesearch.indexer.shared;

import java.io.Serializable;
import java.util.List;


/**
 * @author Samuel Kogler
 * Represents all the data that is needed by the Dashboard page.
 * Used to bundle all the data into a single request.
 */
public class DashboardData implements Serializable {

	/** All currently running jobs. */
	private List<JobStatus> runningJobs;
	/** All the jobs that are scheduled. */
	private List<JobStatus> scheduledJobs;
	
	public List<JobStatus> getScheduledJobs() {
		return scheduledJobs;
	}
	public void setScheduledJobs(List<JobStatus> scheduledJobs) {
		this.scheduledJobs = scheduledJobs;
	}
	public List<JobStatus> getRunningJobs() {
		return runningJobs;
	}
	public void setRunningJobs(List<JobStatus> runningJobs) {
		this.runningJobs = runningJobs;
	}	
}
