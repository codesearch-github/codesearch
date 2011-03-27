package org.codesearch.indexer.core;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.codesearch.indexer.manager.IndexingManager;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author Samuel Kogler
 */
public class IndexerGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SchedulerFactory.class).to(StdSchedulerFactory.class).in(Singleton.class);
        bind(QuartzGuiceJobFactory.class).in(Singleton.class);
        bind(IndexingManager.class).in(Singleton.class);
        
    }

}
