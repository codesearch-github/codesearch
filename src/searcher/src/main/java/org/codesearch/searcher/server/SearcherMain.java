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

import javax.servlet.ServletContextEvent;

import org.apache.log4j.Logger;
import org.codesearch.commons.CommonsGuiceModule;
import org.quartz.Scheduler;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import java.util.logging.Level;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.spi.JobFactory;

/**
 *
 * @author Samuel Kogler
 */
public class SearcherMain extends GuiceServletContextListener {

    private Injector injector = Guice.createInjector(new CommonsGuiceModule(), new SearcherGuiceModule());
    private static final Logger LOG = Logger.getLogger(SearcherMain.class);

    @Override
    protected Injector getInjector() {
        return injector;
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            Scheduler scheduler = injector.getInstance(Scheduler.class);
            scheduler.setJobFactory(injector.getInstance(JobFactory.class));

            JobDetail jd = JobBuilder.newJob(RefreshJob.class).withIdentity("Refresh Job").build();
            Trigger t = TriggerBuilder.newTrigger().forJob(jd).withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(5)).build();
            scheduler.addJob(jd, true);
            scheduler.scheduleJob(t);
            scheduler.start();
        } catch (SchedulerException ex) {
            LOG.error("Error starting jobs: " + ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        Scheduler scheduler = injector.getInstance(Scheduler.class);
        try {
            scheduler.shutdown();
            Thread.sleep(1000);
        } catch (Exception ex) {
            LOG.warn("Quartz scheduler failed to shutdown: " + ex);
        }
    }
}
