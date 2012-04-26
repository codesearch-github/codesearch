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
package org.codesearch.indexer.client.ui;

import org.codesearch.indexer.client.NamedPlace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

/**
 * The container of all views. Allows to define UI that shows up everywhere.
 * @author Samuel Kogler
 */
public class RootContainer extends Composite implements AcceptsOneWidget {

    private static RootContainerUiBinder uiBinder = GWT.create(RootContainerUiBinder.class);

    interface RootContainerUiBinder extends UiBinder<Widget, RootContainer> {
    }

    @UiField
    LayoutPanel contentPanel;

    public RootContainer(EventBus eventBus) {
        initWidget(uiBinder.createAndBindUi(this));
        eventBus.addHandler(PlaceChangeEvent.TYPE, new WindowTitlePlaceChangeListener("Codesearch Index Admin"));
    }

    /** {@inheritDoc} */
    @Override
    public void setWidget(IsWidget w) {
        contentPanel.clear();
        Widget widget = Widget.asWidgetOrNull(w);
        if (widget != null) {
            contentPanel.add(w);
        }
    }

    private class WindowTitlePlaceChangeListener implements PlaceChangeEvent.Handler {

        private String baseTitle = "";

        public WindowTitlePlaceChangeListener(String baseTitle) {
            this.baseTitle = baseTitle;
        }

        @Override
        public void onPlaceChange(PlaceChangeEvent event) {
            String title = baseTitle;
            if(event.getNewPlace() instanceof NamedPlace) {
                NamedPlace np = (NamedPlace) event.getNewPlace();
                title += " - " + np.getName();
            }
            Window.setTitle(title);
        }
    }
}
