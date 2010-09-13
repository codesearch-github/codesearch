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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.codesearch.commons.configreader.xml.PropertyManager;
import org.codesearch.commons.configreader.xml.dto.JobDto;
import org.codesearch.indexer.tasks.IndexingTask;
import org.quartz.JobDetail;
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
    Map<Long, IndexerJob> activeIndexingThreads;
    /** All predefined/availableIndexingThreads */
    Map<Long, IndexerJob> availableIndexingThreads;
    /* Instantiate a logger */
    private static final Logger log = Logger.getLogger(IndexingManager.class);
    private Scheduler scheduler;
    private PropertyManager pm;

    public IndexingManager() throws SchedulerException {
        pm = new PropertyManager();
        SchedulerFactory sf = new StdSchedulerFactory();
        scheduler = sf.getScheduler();
    }

    public void startScheduler() throws SchedulerException, ConfigurationException {
        List<JobDto> jobs = pm.getJobs();
        for (JobDto job : jobs) {
            JobDetail jobDetail = new JobDetail("Job", "asdf", IndexerJob.class); //TODO write group
            jobDetail.getJobDataMap().put("tasks", job.getTasks());
            Calendar calc = Calendar.getInstance();
            Calendar calc2 = job.getStartDate();
            Trigger trigger = new SimpleTrigger("JobTrigger", "triggerGroup", new Date(job.getStartDate().getTimeInMillis()), null, SimpleTrigger.REPEAT_INDEFINITELY, job.getInterval() * 60000l);
            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();
        }
    }
//    /**
//     * This method takes the proper repo configuration and starts
//     * a new indexing thread.
//     * @param theconfiguration
//     * @return
//     */
//    public Long createIndexingJob(final String theconfiguration) {
//        IndexerJob iThread = new IndexerJob();
//        Long indentifier = 1337l;
//        //TODO: init
//        activeIndexingThreads.put(indentifier, iThread);
//        return indentifier;
//
//    }
//    /**
//     * Activates the given IndexingJob
//     * @param indentifier
//     */
//    public void startIndexingJob(Long indentifier) {
//        if (!activeIndexingThreads.containsKey(indentifier)) {
//            activeIndexingThreads.put(indentifier, availableIndexingThreads.get(indentifier));
//            availableIndexingThreads.get(indentifier).start();
//        }
//    }
//    /**
//     * Suspends the execution of the specified IndexingJob
//     * @param indentifier
//     */
//    public void suspendActiveIndexingJob(Long indentifier) {
//        activeIndexingThreads.get(indentifier).suspendSafely();
//    }
//
//     /**
//     * Resumes the execution of the specified IndexingJob
//     * @param indentifier
//     */
//    public void resumeSuspendedIndexingJob(Long indentifier) {
//        activeIndexingThreads.get(indentifier).resumeSafely();
//    }
    /**
     * Terminates the execution of the specified IndexingJob
     * and reverts all changes this Jobs has caused.
     * @param indentifier
     */
//    public void terminateActiveIndexingJob(Long indentifier) {
//        activeIndexingThreads.remove(indentifier).terminateSafely();
//    }
}
