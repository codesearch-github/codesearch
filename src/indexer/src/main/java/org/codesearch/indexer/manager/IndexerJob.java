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
import org.apache.log4j.Logger;
import org.apache.lucene.util.Version;



/**
 *
 * @author Stiboller Stephan
 */
public class IndexerJob extends Thread {

    /** Indicates if the thread is suspended or not. */
    private boolean threadIsSuspended = false;
    /** Indicates if the thread is terminated or not. */
    private boolean threadIsTerminated = false;
    /** List of ITask assigned to this IndexingJob */
    private LinkedList<ITask> taskList = new LinkedList<ITask>();
    /* Instantiate a logger */
    private static final Logger log = Logger.getLogger(IndexerJob.class);

    /**
     * Suspends the thread in a save way.
     */
    public void suspendSavely() {
        try {
            this.wait();
            threadIsSuspended = true;
        } catch (InterruptedException ex) {
            log.error("Thread has been interrupted during suspend process:" +ex.getMessage());
        }
    }

    /**
     * Resumes the thread.
     * The thread is maybe not instantly killed because the changes of
     * the IndexerJob will be reverted.
     */
    public void resumeSavely() {
        this.notify();
        threadIsSuspended = false;
    }

    /**
     * First suspends and then terminates the thread.
     * The thread is maybe not instantly killed because the changes of
     * the IndexerJob will be reverted and cleaned.
     */
    public void terminateSavely() {
        threadIsSuspended = true;
        threadIsTerminated = true;
    }

    /**
     * Extcuts all Tasks related to this job.
     */
    public void run() {
        for (int i = 0; i < taskList.size(); i++) {
            taskList.get(i).execute(this);
            if(threadIsSuspended = true)
                this.suspendSavely();
        }
    }
}
