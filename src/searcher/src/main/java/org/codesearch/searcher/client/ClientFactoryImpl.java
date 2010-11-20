package org.codesearch.searcher.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;
import org.codesearch.searcher.client.ui.fileview.FileView;
import org.codesearch.searcher.client.ui.fileview.FileViewImpl;
import org.codesearch.searcher.client.ui.searchview.SearchView;
import org.codesearch.searcher.client.ui.searchview.SearchViewImpl;

/**
 * Implementation of the factory.
 * @author Samuel Kogler
 */
public class ClientFactoryImpl implements ClientFactory {

    private final EventBus eventBus = new SimpleEventBus();
    private final PlaceController placeController = new PlaceController(eventBus);
    private final SearchView searchView = new SearchViewImpl();
    private final FileView fileView = new FileViewImpl();

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public PlaceController getPlaceController() {
        return placeController;
    }

    @Override
    public SearchView getSearchView() {
        return searchView;
    }

    @Override
    public FileView getFileView() {
        return fileView;
    }
}
