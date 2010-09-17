package org.codesearch.indexer.manager;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Before;
import org.junit.Test;
import org.quartz.SchedulerException;

/**
 *
 * @author David Froehlich
 */
public class IndexingManagerTest {

    IndexingManager im;

    public IndexingManagerTest() {
    }

    @Before
    public void setUp() throws SchedulerException {
        im = new IndexingManager();
    }

    /**
     * Test of readJobsFromConfig method, of class IndexingManager.
     */
    @Test
    public void testStartScheduler() throws SchedulerException, ConfigurationException {
        im.startScheduler();
    }

    @Test
    public void testCreateIndex() {
        //TODO implement
    }
}
