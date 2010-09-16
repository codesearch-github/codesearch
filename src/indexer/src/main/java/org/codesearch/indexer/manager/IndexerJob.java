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

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.codesearch.commons.configreader.xml.PropertyManager;
import org.codesearch.commons.configreader.xml.dto.TaskDto;
import org.codesearch.indexer.tasks.IndexingTask;
import org.codesearch.indexer.tasks.Task;
import org.codesearch.indexer.tasks.TaskExecutionException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author Stiboller Stephan
 * @author David Froehlich
 */
public class IndexerJob implements Job {

//    /** Indicates if the thread is suspended or not. */
//    private boolean jobIsSuspended = false;
    /** Indicates if the thread is terminated or not. */
    private boolean terminated = false;
    /** List of ITask assigned to this IndexingJob */
    private List<TaskDto> taskList = new LinkedList<TaskDto>();
    /* Instantiate a logger */
    private static final Logger log = Logger.getLogger(IndexerJob.class);
    private PropertyManager propertyM;

//    /**
//     * Suspends the thread in a save way.
//     */
//    public void suspendSafely() {
//        try {
//            this.wait();
//            jobIsSuspended = true;
//        } catch (InterruptedException ex) {
//            log.error("Thread has been interrupted during suspend process:" +ex.getMessage());
//        }
//    }
//    /**
//     * Resumes the thread.
//     * The thread is maybe not instantly killed because the changes of
//     * the IndexerJob will be reverted.
//     */
//    public void resumeSafely() {
//        this.notify();
//        jobIsSuspended = false;
//    }
    /**
     * First suspends and then terminates the thread.
     * The thread is maybe not instantly killed because the changes of
     * the IndexerJob will be reverted and cleaned.
     */
    public void terminateSafely() {
        terminated = true;
    }

//    /**
//     * Extcuts all Tasks related to this job.
//     */
//    public void run() {
//        for (int i = 0; i < taskList.size(); i++) {
//            taskList.get(i).execute();
//            if(jobIsSuspended = true)
//                this.suspendSafely();
//        }
//    }
    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        propertyM = new PropertyManager(); //TODO replace with spring injection
        taskList = (List<TaskDto>) (jec.getJobDetail().getJobDataMap().get("tasks"));
        Task task = null;
        for (TaskDto taskDto : taskList) {
            if (terminated) {
                return;
            }
            switch (taskDto.getType()) {
                case index: {
                    task = new IndexingTask();
                    try {
                        ((IndexingTask) task).setRepository(propertyM.getRepositoryByName(taskDto.getRepositoryName()));
                    } catch (ConfigurationException ex) {
                        java.util.logging.Logger.getLogger(IndexerJob.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                }
                case clear:
                    throw new NotImplementedException();
            }
            try {
                task.execute();
            } catch (TaskExecutionException ex) {
                log.error("Task execution was not completed successfully: " + ex.getMessage());
            }
        }
    }
}
