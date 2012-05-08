package org.codesearch.searcher.server;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Refreshes the index information periodically.
 * @author Samuel Kogler
 */
public class RefreshJob implements Job {

    private static final Logger LOG = Logger.getLogger(RefreshJob.class);
    private SearcherUtil searcherUtil;

    /**
     * @param searcherUtil
     */
    @Inject
    public RefreshJob(SearcherUtil searcherUtil) {
        this.searcherUtil = searcherUtil;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            searcherUtil.refreshIndexInformation();
            LOG.debug("Refreshed index information successfully");
        } catch (InvalidIndexException e) {
            LOG.error("Periodic refresh of index failed; the index is invalid: \n" + e);
        }
    }

}
