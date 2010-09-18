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
import java.util.Map;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.codesearch.commons.configreader.xml.PropertyManager;
import org.codesearch.commons.configreader.xml.dto.JobDto;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author Stiboller Stephan
 * @author David Froehlich
 */
public final class IndexingManager {

    /** All active running Threads */
    Map<Long, IndexerJob> indexingJobs;
    /* Instantiate a logger */
    private static final Logger LOG = Logger.getLogger(IndexingManager.class);
    private Scheduler scheduler;
    private PropertyManager pm;

    public IndexingManager() throws SchedulerException {
        pm = new PropertyManager();
        SchedulerFactory sf = new StdSchedulerFactory();
        scheduler = sf.getScheduler();
    }

    public void stopThread(int i) throws SchedulerException {
        List<JobExecutionContext> currentlyExecutedJobs = (List<JobExecutionContext>) scheduler.getCurrentlyExecutingJobs();
        for(JobExecutionContext jec : currentlyExecutedJobs){
            JobDataMap dataMap = jec.getJobDetail().getJobDataMap();
            int currentIndex = Integer.parseInt(dataMap.getString("id"));
            if(i == currentIndex){
                dataMap.put("terminated", true);
                return;
            }
        }
        throw new JobExecutionException("For stopping specified job could not be found");
    }

    public void startScheduler() throws SchedulerException, ConfigurationException {
        List<JobDto> jobs = pm.getJobs();
        int i = 0;
        for (JobDto job : jobs) {
            JobDetail jobDetail = new JobDetail("Job" + i, "IndexingJobs", IndexerJob.class);
            jobDetail.getJobDataMap().put("tasks", job.getTasks());
            jobDetail.getJobDataMap().put("id", i);
            jobDetail.getJobDataMap().put("terminated", false);
            Trigger trigger = new SimpleTrigger("JobTrigger" + i, "triggerGroup", new Date(job.getStartDate().getTimeInMillis()), null, SimpleTrigger.REPEAT_INDEFINITELY, job.getInterval() * 60000l);
            scheduler.scheduleJob(jobDetail, trigger);
            i++;
        }
        scheduler.start();
    }
}
