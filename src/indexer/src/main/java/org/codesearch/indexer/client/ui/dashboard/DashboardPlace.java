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
package org.codesearch.indexer.client.ui.dashboard;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * The place token representing the default page called dashboard.
 *
 * @author Samuel Kogler
 */
public class DashboardPlace extends Place {

    public DashboardPlace() {
    }

    @Prefix("dashboard")
    public static class Tokenizer implements PlaceTokenizer<DashboardPlace> {

        /** {@inheritDoc} */
        @Override
        public String getToken(DashboardPlace place) {
            return "";
        }

        /** {@inheritDoc} */
        @Override
        public DashboardPlace getPlace(String token) {
            return new DashboardPlace();
        }
    }
}
