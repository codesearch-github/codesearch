
package org.codesearch.searcher.client;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import org.codesearch.searcher.client.ui.fileview.FileActivity;
import org.codesearch.searcher.client.ui.fileview.FilePlace;
import org.codesearch.searcher.client.ui.searchview.SearchActivity;
import org.codesearch.searcher.client.ui.searchview.SearchPlace;

/**
 * Manages activities in the searcher.
 * @author Samuel Kogler
 */
public class SearcherActivityMapper implements ActivityMapper {

    private ClientFactory clientFactory;

    public SearcherActivityMapper(ClientFactory clientFactory) {
        super();
        this.clientFactory = clientFactory;
    }

    @Override
    public Activity getActivity(Place place) {
        if(place instanceof SearchPlace) {
            return new SearchActivity(clientFactory);
        }
        if(place instanceof FilePlace) {
            return new FileActivity(clientFactory);
        }

        return null;
    }

}
