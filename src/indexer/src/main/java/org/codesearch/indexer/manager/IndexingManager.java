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
