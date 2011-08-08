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

import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.codesearch.commons.configuration.xml.dto.IndexingTaskType;
import org.codesearch.commons.configuration.xml.dto.TaskDto;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.JobBuilder.*;
import org.quartz.listeners.TriggerListenerSupport;

/**
 * Listener that gets called before the execution of every job and checks
 * whether another job is running and if those jobs have the same tasks
 * @author David Froehlich
 */
public class IndexingJobTriggerListener extends TriggerListenerSupport {

    public static final String LISTENER_NAME = "IndexingJobTriggerListener";
    private Scheduler scheduler;
    private long delayTime;
    private static final Logger LOG = Logger.getLogger(IndexingJobTriggerListener.class);

    /**
     * Creates a new instance of IndexingJobTriggerListener
     * @param scheduler the scheduler that holds all the jobs
     * @param delayTime the time a job should be delayed if another job is currently running (in milliseconds)
     */
    public IndexingJobTriggerListener(Scheduler scheduler, long delayTime) {
        this.scheduler = scheduler;
        this.delayTime = delayTime;
    }

    @Override
    public String getName() {
        return LISTENER_NAME;
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        JobDetail oldDetail = context.getJobDetail();
        try {
            List<JobExecutionContext> currentlyExecutingJobs = scheduler.getCurrentlyExecutingJobs();

            if (!currentlyExecutingJobs.isEmpty()) {
                //TODO make check if currently executing job has redundant tasks
                //in case another indexing job is currently running
                //check for common tasks and remove those from the new jobs list
                
                List<IndexingTaskType> jobTasks = (List<IndexingTaskType>) oldDetail.getJobDataMap().get(IndexingJob.FIELD_TASKS);
                
                List<TaskDto> otherJobsTasks = (List<TaskDto>) currentlyExecutingJobs.get(0).getJobDetail().getJobDataMap().get(IndexingJob.FIELD_TASKS);
                
                for (TaskDto currentTask : otherJobsTasks) {
                    if (jobTasks.contains(currentTask)) {
                        jobTasks.remove(currentTask);
                        LOG.info("Removed IndexingTask " + currentTask.toString() + " from IndexingJob since an indexing process with the same target repository is currently being executed");
                    }
                }
                if (jobTasks.isEmpty()) {
                    LOG.info("No tasks left for execution of IndexingJob, cancelled execution");
                    oldDetail.getJobDataMap().put(IndexingJob.FIELD_TERMINATED, true);
                } else {
                    LOG.info("Execution of IndexingJob aborted, another IndexingJob is currently running, will try to delay execution by " + this.delayTime / 1000 + " seconds");
                    //create trigger by using the methods included via the static import
                    JobDetail newJobDetail = (JobDetail) newJob()
                            .withIdentity(oldDetail.getKey())
                            .usingJobData(oldDetail.getJobDataMap());

                    Trigger newTrigger = (Trigger) newTrigger()
                            .withIdentity("re_trigg_" + new Date().getTime())
                            .startAt(new Date(trigger.getStartTime().getTime() + this.delayTime))
                            .forJob(newJobDetail.getKey()).build();

                    //since scheduler.reschedule doesn't work at this point a new job is created
                    scheduler.scheduleJob(newJobDetail, newTrigger);
                }
                LOG.info("Execution of IndexingJob aborted, another IndexingJob is currently running, will try to delay execution by " + this.delayTime / 1000 + " seconds");
//                    //create trigger by using the methods included via the static import
                    JobDetail newJobDetail = newJob(oldDetail.getJobClass())
                            .withIdentity("re_job_" + new Date().getTime())
                            .usingJobData(oldDetail.getJobDataMap())
                            .build();

                    Trigger newTrigger = (Trigger) newTrigger()
                            .withIdentity("re_trigg_" + new Date().getTime())
                            .startAt(new Date(trigger.getStartTime().getTime() + this.delayTime))
                            .forJob(newJobDetail.getKey())
                            .build();

                    //since scheduler.reschedule doesn't work at this point a new job is created
                    scheduler.scheduleJob(newJobDetail, newTrigger);
                return true;
            }
            return false;
        } catch (SchedulerException ex) {
            LOG.error("Scheduler failed:\n" + ex);
            return true;
        }
    }
}
