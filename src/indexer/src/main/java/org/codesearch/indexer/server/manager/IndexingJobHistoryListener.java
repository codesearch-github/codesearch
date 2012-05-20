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
 * Job listener that maintains a history of job executions.
 *
 * @author Samuel Kogler
 *
 */
public class IndexingJobHistoryListener implements JobListener {

    private List<String> historyMessages = new LinkedList<String>();

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.getClass().getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        String message = context.getJobDetail().getKey() + " started.";
        createHistoryMessage(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        String message = context.getJobDetail().getKey() + " delayed because a job is currently running.";
        createHistoryMessage(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        String message = context.getJobDetail().getKey() + " finished ";
        if (jobException == null) {
            message += "successfully.";
        } else {
            message += "with an error. See log file for details.";
        }
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
