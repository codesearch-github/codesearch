package org.codesearch.searcher.server;

import com.google.inject.servlet.ServletModule;
import org.codesearch.searcher.server.rpc.SearcherServiceImpl;

/**
 *
 * @author Samuel Kogler
 */
public class SearcherGuiceModule extends ServletModule {

    @Override
    protected void configureServlets() {
        serve("/gwt.rpc").with(SearcherServiceImpl.class);
    }
}
