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
import org.codesearch.indexer.shared.RepositoryStatus;
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
    public DashboardServiceImpl(IndexingManager indexingManager) {
        this.indexingManager = indexingManager;
    }

    /** {@inheritDoc} */
    @Override
    public DashboardData getData() throws DashboardServiceException {
        try {
            DashboardData dashboardData = new DashboardData();

            List<JobStatus> runningJobs = indexingManager.getRunningJobs();
            List<JobStatus> scheduledJobs = indexingManager.getScheduledJobs();
            List<JobStatus> delayedJobs = indexingManager.getDelayedJobs();
            List<RepositoryStatus> repositoryStatuses = indexingManager.getRepositoryStatuses();

            dashboardData.setRunningJobs(runningJobs);
            dashboardData.setScheduledJobs(scheduledJobs);
            dashboardData.setDelayedJobs(delayedJobs);
            dashboardData.setRepositoryStatuses(repositoryStatuses);

            return dashboardData;
        } catch (SchedulerException e) {
            throw new DashboardServiceException(e.toString());
        }
    }
}
