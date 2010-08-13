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

import java.util.Map;

/**
 *
 * @author Stiboller Stephan
 */
public class IndexingManager {

    /** All active running Threads */
    Map<Long, IndexerJob> activeIndexingThreads;
    /** All predefined/availableIndexingThreads */
    Map<Long, IndexerJob> availableIndexingThreads;

    /**
     * This method takes the proper repo configuration and starts
     * a new indexing thread.
     * @param theconfiguration
     * @return
     */
    public Long createIndexingJob(final String theconfiguration) {
        IndexerJob iThread = null;
        Long indentifier = null;
        //TODO: init
        activeIndexingThreads.put(indentifier, iThread);
        return indentifier;

    }

    /**
     * Activates the given IndexingJob
     * @param indentifier
     */
    public void startIndexingJob(Long indentifier) {
        if (!activeIndexingThreads.containsKey(indentifier)) {
            activeIndexingThreads.put(indentifier, availableIndexingThreads.get(indentifier));
            availableIndexingThreads.get(indentifier).start();
        }
    }

    /**
     * Suspends the execution of the specified IndexingJob
     * @param indentifier
     */
    public void suspendActiveIndexingJob(Long indentifier) {
        activeIndexingThreads.get(indentifier).suspendSavely();
        //TODO:
    }


    /**
     * Terminates the execution of the specified IndexingJob
     * and reverts all changes this Jobs has caused.
     * @param indentifier
     */
    public void terminateActiveIndexingJob(Long indentifier) {
        activeIndexingThreads.remove(indentifier).terminateSavely();
    }
}
