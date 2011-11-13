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
package org.codesearch.indexer.shared;

import java.io.Serializable;
import java.util.List;

/**
 * @author Samuel Kogler
 * Represents all the data that is needed by the Dashboard page.
 * Used to bundle all the data into a single request.
 */
public class DashboardData implements Serializable {

    /** . */
    private static final long serialVersionUID = -7632905977656885275L;
    /** All currently running jobs. */
    private List<JobStatus> runningJobs;
    /** All the jobs that are scheduled. */
    private List<JobStatus> scheduledJobs;
    /** All the repositories with their indexing statuses */
    private List<RepositoryStatus> repositoryStatuses;

    public List<RepositoryStatus> getRepositoryStatuses() {
        return repositoryStatuses;
    }

    public void setRepositoryStatuses(List<RepositoryStatus> repositoryStatuses) {
        this.repositoryStatuses = repositoryStatuses;
    }
    
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
