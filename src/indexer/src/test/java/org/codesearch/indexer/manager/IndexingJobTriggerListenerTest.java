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

import org.codesearch.commons.configuration.dto.RepositoryDto;
import org.codesearch.indexer.server.manager.IndexingJob;
import org.codesearch.indexer.server.manager.IndexingJobTriggerListener;
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
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            IndexingJobTriggerListener listener = new IndexingJobTriggerListener(1100);
            scheduler.getListenerManager().addTriggerListener(listener, EverythingMatcher.allTriggers());
            
            RepositoryDto repositoryDto1 = new RepositoryDto();
            repositoryDto1.setName("testrepo1");
            
            RepositoryDto repositoryDto2 = new RepositoryDto();
            repositoryDto1.setName("testrepo2");
            
            List<RepositoryDto> repos1 = new LinkedList<RepositoryDto>();
            repos1.add(repositoryDto1);
            repos1.add(repositoryDto2);
            
            List<RepositoryDto> repos2 = new LinkedList<RepositoryDto>();
            repos2.add(repositoryDto2);

            JobDetail job1 = newJob(MockIndexingJob.class)
                    .withIdentity("job1")
                    .build();

            job1.getJobDataMap().put(IndexingJob.FIELD_REPOSITORIES, repos1);
            job1.getJobDataMap().put(MockIndexingJob.TIME_FINISHED, new Date());

            JobDetail job2 = newJob(MockIndexingJob.class)
                    .withIdentity("job2")
                    .build();

            job2.getJobDataMap().put(IndexingJob.FIELD_REPOSITORIES, repos2);
            job2.getJobDataMap().put(MockIndexingJob.TIME_FINISHED, new Date());

            Date now = new Date();
            Date dt1 = new Date(now.getTime() + 1000L);
            Date dt2 = new Date(now.getTime() + 1500L);
            
            Trigger t1 = newTrigger().withIdentity("trigger1")
                    .forJob("job1").startAt(dt1)
                    .build();
            
            Trigger t2 = newTrigger().withIdentity("trigger2")
                    .forJob("job2").startAt(dt2)
                    .build();

            scheduler.scheduleJob(job1, t1);
            scheduler.scheduleJob(job2, t2);

            scheduler.start();

            Thread.sleep(5000);
            Date timeFinished1 = (Date) job1.getJobDataMap().get(MockIndexingJob.TIME_FINISHED);
            Date timeFinished2 = (Date) job2.getJobDataMap().get(MockIndexingJob.TIME_FINISHED);
            assert(timeFinished1.getTime() < timeFinished2.getTime() + 500);
        } catch (InterruptedException ex) {
        }
    }
}
