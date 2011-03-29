package org.codesearch.searcher.server;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import org.codesearch.searcher.server.rpc.SearcherServiceImpl;
import org.codesearch.searcher.server.servlets.UpdateIndexerServlet;

/**
 *
 * @author Samuel Kogler
 */
public class SearcherGuiceModule extends ServletModule {

    @Override
    protected void configureServlets() {
        bind(DocumentSearcher.class).to(DocumentSearcherImpl.class).in(Singleton.class);
        serve("/gwt.rpc").with(SearcherServiceImpl.class);
        serve("/updateIndexer").with(UpdateIndexerServlet.class);
    }
}
