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

package org.codesearch.indexer.core;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.apache.log4j.Logger;
import org.codesearch.indexer.manager.IndexingManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;


/**
 *
 * @author Samuel Kogler
 */
public class IndexerGuiceModule extends AbstractModule {

    public static Logger LOG = Logger.getLogger(IndexerGuiceModule.class);

    @Override
    protected void configure() {
        try {
            bind(Scheduler.class).toInstance(StdSchedulerFactory.getDefaultScheduler());
        } catch (SchedulerException ex) {
            LOG.error("Could not instantiate scheduler: " + ex);
        }
        bind(JobFactory.class).to(QuartzGuiceJobFactory.class).in(Singleton.class);
        bind(IndexingManager.class).in(Singleton.class);
    }
}
