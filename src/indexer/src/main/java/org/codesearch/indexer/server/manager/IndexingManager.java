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
package org.codesearch.indexer.server.manager;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javax.inject.Singleton;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.configuration.dto.JobDto;
import org.codesearch.commons.configuration.dto.RepositoryDto;
import org.codesearch.commons.configuration.properties.IndexStatusManager;
import org.codesearch.commons.constants.IndexConstants;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;
import org.codesearch.indexer.shared.JobStatus;
import org.codesearch.indexer.shared.JobStatusStep;
import org.codesearch.indexer.shared.RepositoryStatus;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.EverythingMatcher;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.JobFactory;

import com.google.inject.Inject;

/**
 * controls the scheduler used to execute the indexing jobs
 *
 * @author Stiboller Stephan
 * @author David Froehlich
 */
@Singleton
public final class IndexingManager {

    /**
     * Instantiate a logger
     */
    private static final Logger LOG = Logger.getLogger(IndexingManager.class);
    /**
     * The scheduler used to schedule the IndexingJobss
     */
    private Scheduler scheduler;
    /**
     * The configurated jobs.
     */
    private List<JobDto> jobs;
    /**
     * The job listener that maintains a history of job executions.
     */
    private IndexingJobHistoryListener historyListener;
    private ConfigurationReader configReader;
    /**
     * used to read the repository revision status
     */
    private IndexStatusManager indexStatusManager;

    /**
     * Keeps the jobs that have been delayed due to a currently executing job
     * in a Queue.
     */
    private Queue<JobDetail> delayedJobQueue = new LinkedList<JobDetail>();

    /**
     * Creates a new instance of IndexingManager
     *
     * @throws SchedulerException In case the scheduler could not be
     *             instantiated
     * @throws ConfigurationException
     */
    @SuppressWarnings("unchecked")
    @Inject
    public IndexingManager(ConfigurationReader configurationReader, IndexStatusManager indexStatusManager, Scheduler scheduler,
            JobFactory jobFactory) throws SchedulerException {
        this.jobs = configurationReader.getJobs();
        this.scheduler = scheduler;
        this.historyListener = new IndexingJobHistoryListener();
        this.indexStatusManager = indexStatusManager;
        this.configReader = configurationReader;

        scheduler.setJobFactory(jobFactory);
        scheduler.getListenerManager().addTriggerListener(new IndexingJobTriggerListener(this), EverythingMatcher.allTriggers());
        scheduler.getListenerManager().addJobListener(new IndexingJobExecutionListener(this), EverythingMatcher.allJobs());
        scheduler.getListenerManager().addJobListener(historyListener, EverythingMatcher.allJobs());
        start();
    }

    /**
     * Reads the jobs from the configuration and adds them to the scheduler,
     * then starts it
     *
     * @throws SchedulerException if a job could not be added to the scheduler
     *             or if it could not be started
     * @throws ConfigurationException if the configuration could not be read
     */
    public void start() throws SchedulerException {
        LOG.info("Starting scheduler with " + jobs.size() + " jobs");

        int i = 0;
        for (JobDto job : jobs) {
            JobDataMap jdm = new JobDataMap();
            jdm.put(IndexingJob.FIELD_REPOSITORIES, job.getRepositories());
            jdm.put(IndexingJob.FIELD_TERMINATED, false);
            jdm.put(IndexingJob.FIELD_CLEAR_INDEX, job.isClearIndex());

            JobKey jobKey = new JobKey(getJobKey(job, i), IndexingJob.GROUP_NAME);
            JobDetail jobDetail = JobBuilder.newJob(IndexingJob.class).withIdentity(jobKey).usingJobData(jdm).build();

            try {
                Trigger trigger = null;
                if (job.getCronExpression() == null || job.getCronExpression().isEmpty()) {
                    LOG.info(jobKey + " was configured without a cron expression, executing once now.");
                    trigger = TriggerBuilder.newTrigger().forJob(jobKey).startNow().build();
                } else {
                    trigger = TriggerBuilder.newTrigger().forJob(jobKey).withSchedule(
                            CronScheduleBuilder.cronSchedule(job.getCronExpression())).build();
                }

                scheduler.addJob(jobDetail, true);
                scheduler.scheduleJob(trigger);
            } catch (ParseException ex) {
                LOG.error("Indexing job " + i + "for repository ---" + " was configured with invalid cron expression:\n" + ex);
            }
            i++;
        }
        scheduler.start();
    }

    private String getJobKey(JobDto job, int i) {
        String jobDescription = "IndexJob " + i;
        if (StringUtils.isNotBlank(job.getJobDescription())) {
            jobDescription += " - " + job.getJobDescription();
        }
        return jobDescription;
    }

    public List<RepositoryStatus> getRepositoryStatuses() {
        List<RepositoryStatus> repositoryStatuses = new LinkedList<RepositoryStatus>();

        for (RepositoryDto currentDto : configReader.getRepositories()) {
            String revision = indexStatusManager.getStatus(currentDto.getName());
            RepositoryStatus.Status status = RepositoryStatus.Status.INDEXED;
            if (revision.equals(VersionControlPlugin.UNDEFINED_VERSION)) {
                status = RepositoryStatus.Status.EMPTY;
            } else if (revision.equals(IndexConstants.REPOSITORY_STATUS_INCONSISTENT)) {
                status = RepositoryStatus.Status.INCONSISTENT;
            }
            repositoryStatuses.add(new RepositoryStatus(currentDto.getName(), revision, status));
        }
        return repositoryStatuses;
    }

    public List<JobStatus> getRunningJobs() throws SchedulerException {
        List<JobStatus> runningJobs = new LinkedList<JobStatus>();
        List<JobExecutionContext> currentlyExecutedJobs = scheduler.getCurrentlyExecutingJobs();
        for (JobExecutionContext currentJob : currentlyExecutedJobs) {
            JobDataMap jobDataMap = currentJob.getJobDetail().getJobDataMap();

            JobStatus jobStatus = new JobStatus();
            jobStatus.setName(currentJob.getJobDetail().getKey().getName());
            jobStatus.setStart(currentJob.getFireTime());
            jobStatus.setCurrentRepository(jobDataMap.getString(IndexingJob.FIELD_CURRENT_REPOSITORY));

            @SuppressWarnings("unchecked")
            List<RepositoryDto> repos = (List<RepositoryDto>) jobDataMap.get(IndexingJob.FIELD_REPOSITORIES);
            List<String> repoNames = new LinkedList<String>();
            for (RepositoryDto repositoryDto : repos) {
                repoNames.add(repositoryDto.getName());
            }
            jobStatus.setRepositories(repoNames);

            JobStatusStep jobStatusStep = new JobStatusStep();
            jobStatusStep.setName(jobDataMap.getString(IndexingJob.FIELD_STEP));
            try {
                jobStatusStep.setTotalSteps(jobDataMap.getIntValue(IndexingJob.FIELD_CURRENT_STEPS));
                jobStatusStep.setFinishedSteps(jobDataMap.getIntValue(IndexingJob.FIELD_FINISHED_STEPS));
            } catch (RuntimeException ex) {
                jobStatusStep.setTotalSteps(0);
                jobStatusStep.setFinishedSteps(0);
            }

            jobStatus.setCurrentStep(jobStatusStep);
            runningJobs.add(jobStatus);
        }
        return runningJobs;
    }

    @SuppressWarnings("unchecked")
    public List<JobStatus> getScheduledJobs() throws SchedulerException {
        List<JobStatus> scheduledJobs = new LinkedList<JobStatus>();
        Set<JobKey> scheduledJobKeys = scheduler.getJobKeys(GroupMatcher.groupEquals(IndexingJob.GROUP_NAME));

        for (JobKey jobKey : scheduledJobKeys) {
            JobStatus jobStatus = new JobStatus();

            List<Trigger> triggersOfJob = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);

            Date nextFireTime = new Date(0L);
            Date previousFireTime = new Date(0L);
            for (Trigger trigger : triggersOfJob) {
                if (trigger != null) {
                    if (trigger.getNextFireTime() != null) {
                        if (nextFireTime.before(trigger.getNextFireTime())) {
                            nextFireTime = trigger.getNextFireTime();
                        }
                    }
                    if (trigger.getPreviousFireTime() != null) {
                        if (previousFireTime.before(trigger.getPreviousFireTime())) {
                            previousFireTime = trigger.getPreviousFireTime();
                        }
                    }
                }
            }

            if (nextFireTime.getTime() == 0L) {
                nextFireTime = null;
            }
            if (previousFireTime.getTime() == 0L) {
                previousFireTime = null;
            }

            jobStatus.setName(jobKey.getName());
            jobStatus.setStart(nextFireTime);
            jobStatus.setFinished(previousFireTime);

            scheduledJobs.add(jobStatus);
        }
        return scheduledJobs;
    }


    public List<JobStatus> getDelayedJobs() {
        List<JobStatus> delayedJobsStatus = new LinkedList<JobStatus>();
        for (JobDetail detail : delayedJobQueue) {
            JobStatus status = new JobStatus();
            status.setName(detail.getKey().getName());
            delayedJobsStatus.add(status);
        }
        return delayedJobsStatus;
    }

    /**
     * Returns the log of job executions.
     *
     * @return list of log messages.
     */
    public List<String> getLog() {
        return historyListener.getHistoryMessages();
    }

    /**
     * Stops the scheduler.
     */
    public void stop() {
        try {
            scheduler.shutdown();
            Thread.sleep(1000);
        } catch (Exception ex) {
            LOG.warn("Quartz scheduler failed to shutdown: " + ex);
        }
    }

    /**
     * schedules a job for the given repositories causing them to be indexed
     * once at the time
     *
     * @param repositories the repositories that are to be indexed
     * @param repositoryGroups the repo groups containing the repositories
     * @throws SchedulerException
     */
    public void startJobForRepositories(List<String> repositories, List<String> repositoryGroups, boolean clear) throws SchedulerException {
        JobKey jobKey = new JobKey("manual-job-" + DateFormat.getTimeInstance().format(new Date().getTime()), IndexingJob.GROUP_NAME);
        List<RepositoryDto> repos = new LinkedList<RepositoryDto>();
        for (String currentRepoName : repositories) {
            repos.add(configReader.getRepositoryByName(currentRepoName));
        }

        for (String currentRepoGroup : repositoryGroups) {
            for (String currentRepo : configReader.getRepositoriesForGroup(currentRepoGroup)) {
                RepositoryDto newRepo = configReader.getRepositoryByName(currentRepo);
                if (!repos.contains(newRepo)) {
                    repos.add(newRepo);
                }
            }
        }

        JobDataMap jdm = new JobDataMap();
        jdm.put(IndexingJob.FIELD_REPOSITORIES, repos);
        jdm.put(IndexingJob.FIELD_TERMINATED, false);
        jdm.put(IndexingJob.FIELD_CLEAR_INDEX, clear);

        JobDetail jobDetail = JobBuilder.newJob(IndexingJob.class).withIdentity(jobKey).usingJobData(jdm).build();
        Trigger jobTrigger = TriggerBuilder.newTrigger().forJob(jobKey).startNow().build();
        scheduler.addJob(jobDetail, true);
        scheduler.scheduleJob(jobTrigger);
        LOG.info("Starting manual indexing job for");
    }

    public boolean delayJob(JobDetail jobDetail) {
        boolean isInQueue = false;
        for (JobDetail d : delayedJobQueue) {
            if (d.getKey().equals(jobDetail.getKey())) {
                isInQueue = true;
                break;
            }
        }

        if (isInQueue) {
            return false;
        } else {
            delayedJobQueue.add(jobDetail);
            return true;
        }
    }

    public void executeNextJobInQueue() {
        JobDetail detail = delayedJobQueue.poll();
        if (detail != null) {
            LOG.info("Executing delayed job " + detail.getKey().toString());
            Trigger trigger = TriggerBuilder.newTrigger().forJob(detail).withIdentity("Delayed Trigger").startNow().build();
            try {
                scheduler.scheduleJob(trigger);
            } catch (SchedulerException e) {
                LOG.error("Error re-scheduling delayed job: " + e);
            }
        }
    }
}
