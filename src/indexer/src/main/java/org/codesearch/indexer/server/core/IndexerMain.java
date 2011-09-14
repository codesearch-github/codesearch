package org.codesearch.indexer.server.core;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.codesearch.commons.CommonsGuiceModule;
import org.codesearch.indexer.server.manager.IndexingManager;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * Creates the Guice Injector and handles shutdown cleanup.
 * @author Samuel Kogler
 */
public class IndexerMain extends GuiceServletContextListener implements ServletContextListener {
    
    private Injector injector = Guice.createInjector(new CommonsGuiceModule(), new IndexerGuiceModule());

	/** {@inheritDoc} */
	@Override
	protected Injector getInjector() {
        return injector;
	}

    /** {@inheritDoc} */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        IndexingManager indexingManager = injector.getInstance(IndexingManager.class);
        indexingManager.stop();
    }
}
