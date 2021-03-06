/**
 * Copyright 2010 David Froehlich   <david.froehlich@businesssoftware.at>,
 *                Samuel Kogler     <samuel.kogler@gmail.com>,
 *                Stephan Stiboller <stistc06@htlkaindorf.at>
 *
 * This file is part of Codesearch.
 *
 * Codesearch is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Codesearch is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Codesearch.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.codesearch.searcher.client;

import org.codesearch.searcher.client.ui.fileview.FileActivity;
import org.codesearch.searcher.client.ui.fileview.FilePlace;
import org.codesearch.searcher.client.ui.searchview.SearchActivity;
import org.codesearch.searcher.client.ui.searchview.SearchPlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

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

    /** {@inheritDoc} */
    @Override
    public Activity getActivity(Place place) {
        if(place instanceof SearchPlace) {
            SearchPlace searchPlace = (SearchPlace) place;
            return new SearchActivity(clientFactory, searchPlace);
        }
        if(place instanceof FilePlace) {
            FilePlace filePlace = (FilePlace) place;
            return new FileActivity(clientFactory, filePlace);
        }

        return null;
    }

}
