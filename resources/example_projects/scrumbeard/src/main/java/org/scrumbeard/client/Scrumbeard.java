/**
 * Copyright 2010 Samuel Kogler <samuel.kogler@gmail.com>
 *
 * This file is part of Scrumbeard.
 *
 * Scrumbeard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Scrumbeard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Scrumbeard.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.scrumbeard.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

/**
 *
 * @author daasdingo
 */
public class Scrumbeard implements EntryPoint {

    private DockLayoutPanel rootPageLayout;
    private SplitLayoutPanel pageLayout;
    private Panel header;
    private Label title;

    public Scrumbeard() {
    }

    public void onModuleLoad() {
        pageLayout = new SplitLayoutPanel();
        rootPageLayout = new DockLayoutPanel(Unit.EM);
        header = new FlowPanel();
        title = new Label("Scrumbeard");
        DOM.setElementAttribute(title.getElement(), "class", "title");

        pageLayout.addWest(new HTML("left"), 100);
        pageLayout.addEast(new HTML("right"), 100);
        pageLayout.add(new HTML("center"));

        header.add(title);

        rootPageLayout.addNorth(header, 5);
        rootPageLayout.add(pageLayout);

        RootLayoutPanel.get().add(rootPageLayout);
    }
}
