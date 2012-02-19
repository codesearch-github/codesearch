package org.codesearch.indexer.client;

import com.google.gwt.place.shared.Place;

/**
 * Place class that has a name property.
 * @author Samuel Kogler
 */
public abstract class NamedPlace extends Place {
    /**
     * Returns the name of the place which can be displayed in the UI.
     * @return The name of the place
     */
    public abstract String getName();
}
