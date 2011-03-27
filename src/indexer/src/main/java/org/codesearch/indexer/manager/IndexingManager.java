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
package org.codesearch.indexer.manager;

import com.google.inject.Inject;
import com.google.inject.Injector;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.configuration.xml.dto.JobDto;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;

/**
 * controls the scheduler used to execute the indexing jobs
 * @author Stiboller Stephan
 * @author David Froehlich
 */
public final class IndexingManager {

    /** Instantiate a logger */
    private static final Logger LOG = Logger.getLogger(IndexingManager.class);
    /** The scheduler used to schedule the IndexingJobss */
    private Scheduler scheduler;
    /** The configurated jobs. */
    private List<JobDto> jobs;

    /**
     * Creates a new instance of IndexingManager
     * @throws SchedulerException In case the scheduler could not be instantiated
     */
    @Inject
    public IndexingManager(ConfigurationReader configurationReader, SchedulerFactory sf, JobFactory jobFactory) throws SchedulerException {
        jobs = configurationReader.getJobs();
        scheduler = sf.getScheduler();
        scheduler.setJobFactory(jobFactory);
    }

    public Map<Integer, IndexingJobDto> getCurrentStatus() throws SchedulerException {
        Map<Integer, IndexingJobDto> current_jobs = new HashMap<Integer, IndexingJobDto>();
        List<JobExecutionContext> currentlyExecutedJobs = (List<JobExecutionContext>) scheduler.getCurrentlyExecutingJobs();
        for(JobExecutionContext currentJob : currentlyExecutedJobs) {
            int index = Integer.parseInt(currentJob.getJobDetail().getJobDataMap().getString(IndexingJob.FIELD_ID));
            int tasksFinished = currentJob.getJobDetail().getJobDataMap().getInt(IndexingJob.FIELD_TASKS_FINISHED);
            int tasksTotal = ((List)currentJob.getJobDetail().getJobDataMap().get(IndexingJob.FIELD_TASKS)).size();
            Date timeStarted = currentJob.getFireTime();
            String type = currentJob.getJobDetail().getJobDataMap().getString(IndexingJob.FIELD_CURRENT_TYPE);
            String currentlyAccessedRepository = currentJob.getJobDetail().getJobDataMap().getString(IndexingJob.FIELD_CURRENT_REPOSITORY);
            IndexingJobDto currentDto = new IndexingJobDto(type, timeStarted, tasksTotal, tasksFinished, currentlyAccessedRepository);
            current_jobs.put(index, currentDto);
        }
        return current_jobs;
    }

    /**
     * Reads the jobs from the configuration and adds them to the scheduler, then starts it
     * @throws SchedulerException if a job could not be added to the scheduler or if it could not be started
     * @throws ConfigurationException if the configuration could not be read
     */
    public void startScheduler() throws SchedulerException, ConfigurationException {
        LOG.debug("Starting scheduler");
        int i = 0;
        LOG.info("Starting scheduler with " + jobs.size() + " jobs");

        for (JobDto job : jobs) {
            JobDetail jobDetail = new JobDetail("Job" + i, "IndexingJobs", IndexingJob.class);
            jobDetail.getJobDataMap().put("tasks", job.getTasks());
            jobDetail.getJobDataMap().put("id", i);

            jobDetail.getJobDataMap().put("terminated", false);

            Trigger trigger;
            if (job.getInterval() == 0) {
                LOG.info("setting job for single execution");
                trigger = new SimpleTrigger("JobTrigger" + i, new Date(job.getStartDate().getTimeInMillis()), null, 0, 0);
            } else {
                trigger = new SimpleTrigger("JobTrigger" + i, new Date(job.getStartDate().getTimeInMillis()), null, SimpleTrigger.REPEAT_INDEFINITELY, job.getInterval() * 60000l);
            }
            scheduler.scheduleJob(jobDetail, trigger);
            i++;
        }
        scheduler.start();
    }

     /**
     * Flags the job as terminated which causes it to stop after the execution of the current task or throws a JobExecutionException if the task could not be found
     * @param i the id of the job that is to be terminated
     * @throws SchedulerException if there is no job found with the id or if the jobs could not be read from the scheduler
     */
    public void terminateJob(int i) throws SchedulerException {
        List<JobExecutionContext> currentlyExecutedJobs = (List<JobExecutionContext>) scheduler.getCurrentlyExecutingJobs();
        for (JobExecutionContext jec : currentlyExecutedJobs) {
            JobDataMap dataMap = jec.getJobDetail().getJobDataMap();
            int currentIndex = Integer.parseInt(dataMap.getString(IndexingJob.FIELD_ID));
            if (i == currentIndex) {
                dataMap.put(IndexingJob.FIELD_TERMINATED, true);
                return;
            }
        }
        throw new JobExecutionException("Job specified for termination could not be found");
    }
}
