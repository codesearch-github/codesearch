package org.codesearch.indexer.shared;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Samuel Kogler
 * Represents the status of a configured Job.
 */
/**
 * @author Samuel Kogler
 *
 */
/**
 * @author Samuel Kogler
 *
 */
/**
 * @author Samuel Kogler
 *
 */
public class JobStatus implements Serializable {

	/**
	 * The different possible values for the status.
	 */
	public enum Status {
		CLEARING,
		INDEXING,
		INACTIVE
	};
	
	/** The name of the job. */
	private String name;
	/** The current status. */
	private Status status;
	/** The start date, can be in the future for scheduled jobs
	 *  or in the past for finished or failed jobs. */
	private Date start;
	/** The date when the job was finished, null if not yet finished. */
	private Date finished;
	/** The name of the repository currently being processed. */
	private String currentRepository;
	/** The number of repositories configured for the job. */
	private int totalRepositories;
	/** The number of currently finished repositories. */
	private int finishedRepositories;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date started) {
		this.start = started;
	}

	public Date getFinished() {
		return finished;
	}

	public void setFinished(Date finished) {
		this.finished = finished;
	}

    public int getFinishedRepositories() {
        return finishedRepositories;
    }

    public void setFinishedRepositories(int finishedRepositories) {
        this.finishedRepositories = finishedRepositories;
    }

    public int getTotalRepositories() {
        return totalRepositories;
    }

    public void setTotalRepositories(int totalRepositories) {
        this.totalRepositories = totalRepositories;
    }

    public String getCurrentRepository() {
        return currentRepository;
    }

    public void setCurrentRepository(String currentRepository) {
        this.currentRepository = currentRepository;
    }
}
