/**
 * 
 */
package org.codesearch.indexer.server.manager;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;


/**
 * 
 * Job listener that mantains a history of job executions.
 * @author Samuel Kogler
 *
 */
public class IndexingJobHistoryListener implements JobListener {

    private List<String> historyMessages = new LinkedList<String>();
    
    /** {@inheritDoc} */
    @Override
    public String getName() {
        return this.getClass().getName();
    }

    /** {@inheritDoc} */
    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        String message = context.getJobDetail().getKey() + " started.";
        createHistoryMessage(message);
    }

    /** {@inheritDoc} */
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {

    }

    /** {@inheritDoc} */
    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        String message = context.getJobDetail().getKey() + " finished.";
        createHistoryMessage(message);
    }
    
    /**
     * @return the historyMessages
     */
    public List<String> getHistoryMessages() {
        return historyMessages;
    }

    private void createHistoryMessage(String text) {
        StringBuilder message = new StringBuilder();
        DateFormat df = DateFormat.getDateTimeInstance();
        Date now = new Date();
        message.append(df.format(now));
        message.append(" - ");
        message.append(text);
        historyMessages.add(message.toString());
    }

}
