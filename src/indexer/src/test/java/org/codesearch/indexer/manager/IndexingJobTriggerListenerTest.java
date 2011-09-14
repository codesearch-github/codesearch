/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.indexer.manager;


import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.codesearch.commons.configuration.xml.dto.IndexingTaskType;
import org.codesearch.indexer.server.manager.IndexingJob;
import org.codesearch.indexer.server.manager.IndexingJobTriggerListener;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.EverythingMatcher;

/**
 *
 * @author david
 */
public class IndexingJobTriggerListenerTest {

    /**
     * Test of vetoJobExecution method, of class IndexingJobTriggerListener.
     */
    @Test
    public void testVetoJobExecution() throws SchedulerException {
    	//FIXME update this unit test
//        try {
//            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
//            IndexingJobTriggerListener listener = new IndexingJobTriggerListener(scheduler, 1100);
//            scheduler.getListenerManager().addTriggerListener(listener, EverythingMatcher.allTriggers());
//
//            JobDetail job1 = newJob(MockIndexingJob.class)
//                    .withIdentity("job1")
//                    .build();
//
//            List<IndexingTaskType> jl1 = new LinkedList<IndexingTaskType>();
//
//            job1.getJobDataMap().put(IndexingJob.FIELD_TASKS, jl1);
//            job1.getJobDataMap().put(MockIndexingJob.TIME_FINISHED, new Date());
//
//            JobDetail job2 = newJob(MockIndexingJob.class)
//                    .withIdentity("job2")
//                    .build();
//            List<IndexingTaskType> jl2 = new LinkedList<IndexingTaskType>();
//
//            job2.getJobDataMap().put(IndexingJob.FIELD_TASKS, jl2);
//            job2.getJobDataMap().put(MockIndexingJob.TIME_FINISHED, new Date());
//
//            Trigger t1 = newTrigger().withIdentity("trigger1")
//                    .forJob("job1")
//                    .build();
//            Trigger t2 = newTrigger().withIdentity("trigger2")
//                    .forJob("job2")
//                    .build();
//
//            scheduler.scheduleJob(job1, t1);
//            scheduler.scheduleJob(job2, t2);
//
//            scheduler.start();
//
//            Thread.sleep(2000);
//            Date timeFinished1 = (Date) job1.getJobDataMap().get(MockIndexingJob.TIME_FINISHED);
//            Date timeFinished2 = (Date) job2.getJobDataMap().get(MockIndexingJob.TIME_FINISHED);
//            assert(timeFinished1.getTime() < timeFinished2.getTime() + 500);
//        } catch (InterruptedException ex) {
//        }
    }
}
