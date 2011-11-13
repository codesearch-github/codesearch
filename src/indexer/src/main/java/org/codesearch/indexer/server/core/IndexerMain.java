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
