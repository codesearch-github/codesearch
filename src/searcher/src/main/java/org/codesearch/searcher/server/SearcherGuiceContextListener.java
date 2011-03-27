package org.codesearch.searcher.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import org.codesearch.commons.CommonsGuiceModule;

/**
 *
 * @author Samuel Kogler
 */
public class SearcherGuiceContextListener extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new CommonsGuiceModule(), new SearcherGuiceModule());
    }
}
