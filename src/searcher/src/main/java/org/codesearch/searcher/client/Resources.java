package org.codesearch.searcher.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;

/**
 * Interface holding all the resources needed for the searcher UI.
 * @author Samuel Kogler
 */
public interface Resources extends ClientBundle {

    public static final Resources INSTANCE = GWT.create(Resources.class);

    @Source("codesearch.png")
    @ImageOptions(height = 90)
    public ImageResource logo();
}
