/**
 * Copyright 2010 David Froehlich <david.froehlich@businesssoftware.at>, Samuel Kogler <samuel.kogler@gmail.com>, Stephan Stiboller
 * <stistc06@htlkaindorf.at>
 *
 * This file is part of Codesearch.
 *
 * Codesearch is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Codesearch is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Codesearch. If not, see <http://www.gnu.org/licenses/>.
 */
package org.codesearch.indexer.server.manager;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.codesearch.commons.configuration.dto.RepositoryDto;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.quartz.listeners.TriggerListenerSupport;

/**
 * Listener that gets called before the execution of every job and checks whether another job is running and if those jobs have the same
 * tasks
 *
 * @author David Froehlich
 */
public class IndexingJobTriggerListener extends TriggerListenerSupport implements TriggerListener {
    private static final Logger LOG = Logger.getLogger(IndexingJobTriggerListener.class);
    private IndexingManager indexingManager;

    /**
     * Creates a new instance of IndexingJobTriggerListener
     *
     * @param delayTime the time a job should be delayed if another job is currently running (in milliseconds)
     */
    @Inject
    public IndexingJobTriggerListener(IndexingManager indexingManager) {
        this.indexingManager = indexingManager;
    }

    @Override
    public String getName() {
        return IndexingJobTriggerListener.class.getName();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        Scheduler scheduler = context.getScheduler();
        JobDetail jobDetail = context.getJobDetail();

        try {
            List<JobExecutionContext> currentlyExecutingJobs = scheduler.getCurrentlyExecutingJobs();

            LOG.debug("Checking if a job is currently running");
            LOG.debug(currentlyExecutingJobs.size() + " jobs currently running");

            if (!currentlyExecutingJobs.isEmpty()) {
                LOG.info("Delaying execution of job " + jobDetail.getKey().toString());
                JobDetail newJobDetail = newJob(jobDetail.getJobClass()).withIdentity(jobDetail.getKey())
                        .usingJobData(jobDetail.getJobDataMap()).build();
                indexingManager.delayJob(newJobDetail);
                return true;
            } else {
                return false;
            }
        } catch (SchedulerException ex) {
            LOG.error("Scheduler failed:\n" + ex);
            return true;
        }
    }
}
