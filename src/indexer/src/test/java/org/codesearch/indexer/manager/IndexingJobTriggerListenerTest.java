/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.indexer.manager;


import static org.quartz.TriggerBuilder.*;
import static org.quartz.JobBuilder.*;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codesearch.commons.configuration.xml.dto.IndexingTaskType;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.plugins.vcs.AuthenticationType;
import org.codesearch.commons.plugins.vcs.NoAuthentication;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.quartz.JobDetail;
import static org.junit.Assert.*;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author david
 */
public class IndexingJobTriggerListenerTest {
    
    public IndexingJobTriggerListenerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of vetoJobExecution method, of class IndexingJobTriggerListener.
     */
    @Test
    public void testVetoJobExecution() throws SchedulerException {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            IndexingJobTriggerListener listener = new IndexingJobTriggerListener(scheduler, 1100);
            scheduler.getListenerManager().addTriggerListener(listener);
            
            JobDetail job1 = newJob(MockIndexingJob.class)
                    .withIdentity("job1")
                    .build();
            
            List<IndexingTaskType> jl1 = new LinkedList<IndexingTaskType>();
            
            job1.getJobDataMap().put(IndexingJob.FIELD_TASKS, jl1);
            job1.getJobDataMap().put(MockIndexingJob.TIME_FINISHED, new Date());
            
            JobDetail job2 = newJob(MockIndexingJob.class)
                    .withIdentity("job2")
                    .build();
            List<IndexingTaskType> jl2 = new LinkedList<IndexingTaskType>();
            
            job2.getJobDataMap().put(IndexingJob.FIELD_TASKS, jl2);
            job2.getJobDataMap().put(MockIndexingJob.TIME_FINISHED, new Date());
            
            Trigger t1 = newTrigger().withIdentity("trigger1")
                    .forJob("job1")
                    .build();
            Trigger t2 = newTrigger().withIdentity("trigger2")
                    .forJob("job2")
                    .build();
            
            scheduler.scheduleJob(job1, t1);
            scheduler.scheduleJob(job2, t2);
                    
            scheduler.start();
            
            Thread.sleep(2000);
            Date timeFinished1 = (Date) job1.getJobDataMap().get(MockIndexingJob.TIME_FINISHED);
            Date timeFinished2 = (Date) job2.getJobDataMap().get(MockIndexingJob.TIME_FINISHED);
            assert(timeFinished1.getTime() < timeFinished2.getTime() + 500);
        } catch (InterruptedException ex) {
        }
    }
}
