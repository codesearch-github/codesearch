/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.indexer.manager;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.codesearch.commons.configreader.xml.dto.JobDto;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.quartz.SchedulerException;

/**
 *
 * @author David Froehlich
 */
public class IndexingManagerTest {
    IndexingManager im;
    public IndexingManagerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        try {
            im = new IndexingManager();
        } catch (SchedulerException ex) {
            fail("SchedulerException"+ ex.getMessage());
        }
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of readJobsFromConfig method, of class IndexingManager.
     */
    @Test
    public void testStartScheduler(){
        System.out.println("startScheduler");
        try {
            im.startScheduler();
        } catch (SchedulerException ex) {
            fail("SchedulerException "+ex.getMessage());
        } catch (ConfigurationException ex) {
            fail("ConfigurationException "+ex.getMessage());
        }
    }
}