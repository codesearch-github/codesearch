/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.indexer.manager;

import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * A mockup indexing job that always takes ~ 1 second to execute
 * The time at which the job finished can be retrieved via the JobExecutionContext
 * @author david
 */
public class MockIndexingJob implements Job {
    public static final String TIME_FINISHED = "time finished";
    private static final Logger LOG = Logger.getLogger(MockIndexingJob.class);
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            LOG.info("Mockup test executing");
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            
        }
        
        Date finished = (Date) context.getJobDetail().getJobDataMap().get(TIME_FINISHED);
        finished.setTime(new Date().getTime());
    }
    
}
