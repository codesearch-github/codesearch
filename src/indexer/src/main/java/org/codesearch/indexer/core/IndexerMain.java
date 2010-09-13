/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.indexer.core;

import javax.servlet.ServletContextEvent;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.codesearch.indexer.manager.IndexingManager;
import org.quartz.SchedulerException;

/**
 *
 * @author David Froehlich
 */
public class IndexerMain implements javax.servlet.ServletContextListener{
    protected static final Logger log = Logger.getLogger(IndexerMain.class);
    private IndexingManager indexingManager;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("blargh");
        try {
            indexingManager = new IndexingManager();
            indexingManager.startScheduler();
        } catch (SchedulerException ex) {
            log.error("Problem with scheduler at context initialization: "+ex.getMessage());
        } catch (ConfigurationException ex) {
            log.error("Problem with configuration at context initialization: "+ex.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
