/**
 * Copyright 2010 David Froehlich <david.froehlich@businesssoftware.at>, Samuel
 * Kogler <samuel.kogler@gmail.com>, Stephan Stiboller <stistc06@htlkaindorf.at>
 *
 * This file is part of Codesearch.
 *
 * Codesearch is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Codesearch is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Codesearch. If not, see <http://www.gnu.org/licenses/>.
 */
package org.codesearch.searcher.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.codesearch.searcher.server.rpc.SearcherServiceImpl;
import org.codesearch.searcher.server.servlets.OpenSearchServlet;
import org.codesearch.searcher.server.servlets.RefreshServlet;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import org.codesearch.commons.utils.QuartzGuiceJobFactory;

/**
 *
 * @author Samuel Kogler
 */
public class SearcherGuiceModule extends ServletModule {

    private static final Logger LOG = Logger.getLogger(SearcherGuiceModule.class);

    @Override
    protected void configureServlets() {
        bind(DocumentSearcher.class).to(DocumentSearcherImpl.class).in(Singleton.class);
        bind(SearcherUtil.class).to(SearcherUtilImpl.class).in(Singleton.class);
        serve("/gwt.rpc").with(SearcherServiceImpl.class);
        serve("/refresh").with(RefreshServlet.class);
        serve("/codesearch-opensearch.xml").with(OpenSearchServlet.class);

        // Initialize quartz scheduler
        try {
            // Get quartz config
            InputStream configFile = this.getClass().getClassLoader().getResourceAsStream("quartz.properties");
            if (configFile == null) {
                throw new IOException("Resource was null");
            }
            Properties properties = new Properties();
            properties.load(configFile);
            // Initialize quartz
            SchedulerFactory schedulerFactory = new StdSchedulerFactory(properties);
            Scheduler scheduler = schedulerFactory.getScheduler();
            bind(Scheduler.class).toInstance(scheduler);
            LOG.debug("Initialized quartz successfully");
        } catch (SchedulerException ex) {
            LOG.error("Could not instantiate scheduler: " + ex);
        } catch (IOException e) {
            LOG.error("Could not find configuration file 'quartz.properties':\n" + e);
        }
    }
}
