package org.codesearch.indexer.server.manager;

import javax.inject.Inject;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

/**
 * @author Samuel Kogler
 *
 */
public class IndexingJobExecutionListener implements JobListener {

    private IndexingManager indexingManager;
    
    /**
     * @param indexingManager
     */
    @Inject
    public IndexingJobExecutionListener(IndexingManager indexingManager) {
        this.indexingManager = indexingManager;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return "Job delay queue listener";
    }

    /** {@inheritDoc} */
    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        // TODO Auto-generated method stub

    }

    /** {@inheritDoc} */
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        // TODO Auto-generated method stub

    }

    /** {@inheritDoc} */
    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        

    }

}
