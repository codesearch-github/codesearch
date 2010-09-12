package org.codesearch.searcher.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import org.codesearch.searcher.client.ui.SearchInterface;

/**
 * The entry point for the searcher.
 * @author Samuel Kogler
 */
public class SearcherEntryPoint implements EntryPoint{

    @Override
    public void onModuleLoad() {
        SearchInterface searchInterface = new SearchInterface();
        RootLayoutPanel.get().add(searchInterface);
    }
}
