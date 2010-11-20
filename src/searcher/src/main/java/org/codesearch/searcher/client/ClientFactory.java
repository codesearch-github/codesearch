package org.codesearch.searcher.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import org.codesearch.searcher.client.ui.fileview.FileView;
import org.codesearch.searcher.client.ui.searchview.SearchView;

/**
 * Provides the implementations of classes used throughout the client.
 * @author Samuel Kogler
 */
public interface ClientFactory {
    EventBus getEventBus();
    PlaceController getPlaceController();
    SearchView getSearchView();
    FileView getFileView();
}
