package org.codesearch.indexer.client;

import org.codesearch.indexer.client.ui.dashboard.DashboardView;
import org.codesearch.indexer.client.ui.log.LogView;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import org.codesearch.indexer.client.ui.manualIndexing.ManualIndexingView;

/**
 *
 * @author Samuel Kogler
 */
public abstract class ClientFactory {

    private static ClientFactory clientFactory;

    public static ClientFactory getDefaultFactory() {
        if (clientFactory == null) {
            clientFactory = new ClientFactoryImpl();
        }
        return clientFactory;
    }

    public abstract EventBus getEventBus();

    public abstract PlaceController getPlaceController();

    public abstract DashboardView getDashboardView();

    public abstract LogView getLogView();

    public abstract ManualIndexingView getManualIndexingView();
}
