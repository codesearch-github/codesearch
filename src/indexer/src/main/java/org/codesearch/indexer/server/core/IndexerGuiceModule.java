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

package org.codesearch.indexer.server.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.codesearch.indexer.server.manager.IndexingManager;
import org.codesearch.indexer.server.rpc.DashboardServiceImpl;
import org.codesearch.indexer.server.rpc.LogServiceImpl;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

/**
 *
 * @author Samuel Kogler
 */
public class IndexerGuiceModule extends ServletModule {

    public static Logger LOG = Logger.getLogger(IndexerGuiceModule.class);

    @Override
    protected void configureServlets() {
        try {
            // Get quartz config
            InputStream configFile = this.getClass().getClassLoader().getResourceAsStream("quartz.properties");
            if(configFile == null) {
                throw new IOException("Resource was null");
            }
            Properties properties = new Properties();
            properties.load(configFile);
            // Initialize quartz
        	SchedulerFactory schedulerFactory = new StdSchedulerFactory(properties);
            bind(Scheduler.class).toInstance(schedulerFactory.getScheduler());
            
            LOG.debug("Initialized quartz successfully");
        } catch (SchedulerException ex) {
            LOG.error("Could not instantiate scheduler: " + ex);
        } catch (IOException e) {
            LOG.error("Could not find configuration file 'quartz.properties':\n" + e);
        }
        bind(JobFactory.class).to(QuartzGuiceJobFactory.class).in(Singleton.class);
        bind(IndexingManager.class).in(Singleton.class);
        serve("/dashboard.rpc").with(DashboardServiceImpl.class);
        serve("/log.rpc").with(LogServiceImpl.class);
    }
}
