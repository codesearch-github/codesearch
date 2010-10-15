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
package org.codesearch.indexer;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.codesearch.commons.configuration.xml.XmlConfigurationReader;
import org.codesearch.commons.configuration.xml.dto.JobDto;
import org.codesearch.commons.plugins.PluginLoader;
import org.codesearch.indexer.jobs.IndexingJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.JobDetailBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerBean;

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
    /** The XmlConfigReader used to retrieve information from the configuration */
    @Autowired
    private XmlConfigurationReader xmlConfigurationReader;
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;
    @Autowired
    private PluginLoader pluginLoader;

    /**
     * Creates a new instance of IndexingManager
     */
    public IndexingManager() {
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
            int currentIndex = Integer.parseInt(dataMap.getString("id"));
            if (i == currentIndex) {
                dataMap.put("terminated", true);
                return;
            }
        }
        throw new JobExecutionException("Job specified for termination could not be found");
    }

    /**
     * Reads the jobs from the configuration and adds them to the scheduler, then starts it
     * @throws SchedulerException if a job could not be added to the scheduler or if it could not be started
     * @throws ConfigurationException if the configuration could not be read
     */
    public void startScheduler() throws SchedulerException, ConfigurationException {
        LOG.debug("Starting scheduler");
        List<JobDto> jobs = xmlConfigurationReader.getJobs();
        LOG.info("Starting scheduler with " + jobs.size() + " jobs");

        List<Trigger> triggers = new LinkedList<Trigger>();

        SimpleTriggerBean trigger = null;
        JobDetailBean jobDetail = null;
        int i = 0;
        for (JobDto job : jobs) {
            jobDetail = new JobDetailBean();
            jobDetail.setBeanName("indexingJob" + i);
            jobDetail.setJobClass(IndexingJob.class);
            jobDetail.getJobDataMap().put("taskList", job.getTasks());
            jobDetail.getJobDataMap().put("terminated", false);
            jobDetail.getJobDataMap().put("xmlConfigurationReader", xmlConfigurationReader);
            jobDetail.getJobDataMap().put("pluginLoader", pluginLoader);

            trigger = new SimpleTriggerBean();
            trigger.setName("JobTrigger" + i);
            trigger.setStartTime(new Date(job.getStartDate().getTimeInMillis()));
            if (job.getInterval() == 0) {
                LOG.info("setting job for single execution");
            } else {
                trigger.setRepeatInterval(job.getInterval() * 60000l);
                trigger.setRepeatCount(SimpleTriggerBean.REPEAT_INDEFINITELY);
            }
            trigger.setJobDetail(jobDetail);
            triggers.add(trigger);
            i++;
        }
        schedulerFactoryBean.setTriggers(triggers.toArray(new Trigger[0]));
        schedulerFactoryBean.stop();
        try {
            schedulerFactoryBean.afterPropertiesSet();
        } catch (Exception ex) {
            LOG.error(ex);
        }
        schedulerFactoryBean.start();
    }

    public void setXmlConfigurationReader(XmlConfigurationReader xmlConfigurationReader) {
        this.xmlConfigurationReader = xmlConfigurationReader;
    }

    public void setSchedulerFactoryBean(SchedulerFactoryBean schedulerFactoryBean) {
        this.schedulerFactoryBean = schedulerFactoryBean;
    }

    public void setPluginLoader(PluginLoader pluginLoader) {
        this.pluginLoader = pluginLoader;
    }
}
