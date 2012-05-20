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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.indexer.manager;


import org.junit.Ignore;
import org.junit.Test;
import org.quartz.SchedulerException;

/**
 *
 * @author david
 */
@Ignore
public class IndexingJobTriggerListenerTest {

    /**
     * Test of vetoJobExecution method, of class IndexingJobTriggerListener.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testVetoJobExecution() throws SchedulerException {
//        try {
//            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
//            IndexingJobTriggerListener listener = new IndexingJobTriggerListener();
//            scheduler.getListenerManager().addTriggerListener(listener, EverythingMatcher.allTriggers());
//
//            RepositoryDto repositoryDto1 = new RepositoryDto();
//            repositoryDto1.setName("testrepo1");
//
//            RepositoryDto repositoryDto2 = new RepositoryDto();
//            repositoryDto1.setName("testrepo2");
//
//            List<RepositoryDto> repos1 = new LinkedList<RepositoryDto>();
//            repos1.add(repositoryDto1);
//            repos1.add(repositoryDto2);
//
//            List<RepositoryDto> repos2 = new LinkedList<RepositoryDto>();
//            repos2.add(repositoryDto2);
//
//            JobDetail job1 = newJob(MockIndexingJob.class)
//                    .withIdentity("job1")
//                    .build();
//
//            job1.getJobDataMap().put(IndexingJob.FIELD_REPOSITORIES, repos1);
//            job1.getJobDataMap().put(MockIndexingJob.TIME_FINISHED, new Date());
//
//            JobDetail job2 = newJob(MockIndexingJob.class)
//                    .withIdentity("job2")
//                    .build();
//
//            job2.getJobDataMap().put(IndexingJob.FIELD_REPOSITORIES, repos2);
//            job2.getJobDataMap().put(MockIndexingJob.TIME_FINISHED, new Date());
//
//            Date now = new Date();
//            Date dt1 = new Date(now.getTime() + 1000L);
//            Date dt2 = new Date(now.getTime() + 1500L);
//
//            Trigger t1 = newTrigger().withIdentity("trigger1")
//                    .forJob("job1").startAt(dt1)
//                    .build();
//
//            Trigger t2 = newTrigger().withIdentity("trigger2")
//                    .forJob("job2").startAt(dt2)
//                    .build();
//
//            scheduler.scheduleJob(job1, t1);
//            scheduler.scheduleJob(job2, t2);
//
//            scheduler.start();
//
//            Thread.sleep(5000);
//            Date timeFinished1 = (Date) job1.getJobDataMap().get(MockIndexingJob.TIME_FINISHED);
//            Date timeFinished2 = (Date) job2.getJobDataMap().get(MockIndexingJob.TIME_FINISHED);
//            assert(timeFinished1.getTime() < timeFinished2.getTime() + 500);
//        } catch (InterruptedException ex) {
//        }
    }
}
