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
package org.codesearch.indexer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Listener class that calls required methods at startup and destruction of the program
 * @author David Froehlich
 */
public class IndexerContextListener implements ServletContextListener {

    /** Logger. */
    protected static final Logger LOG = Logger.getLogger(IndexerContextListener.class);
    
    /**
     * Instantiates the IndexingManager and starts its scheduler
     * @param sce dummy parameter needed by the parent class implementation
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
        try {
            IndexingManager indexingManager = applicationContext.getBean("indexingManager", IndexingManager.class);
            indexingManager.startScheduler();
        } catch (SchedulerException ex) {
            LOG.error("Problem starting scheduler at context initialization: " + ex);
        } catch (ConfigurationException ex) {
            LOG.error("Problem with configuration at context initialization: " + ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
