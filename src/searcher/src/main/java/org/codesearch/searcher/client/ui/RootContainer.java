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
package org.codesearch.searcher.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.codesearch.searcher.client.Resources;

/**
 * The container of all views. Allows to define UI that shows up everywhere.
 * @author Samuel Kogler
 * @author Stiboller Stephan
 */
public class RootContainer extends Composite implements AcceptsOneWidget {

    private static RootContainerUiBinder uiBinder = GWT.create(RootContainerUiBinder.class);

    interface RootContainerUiBinder extends UiBinder<Widget, RootContainer> {
    }
    @UiField
    LayoutPanel contentPanel;
    @UiField
    FlowPanel errorPanel;
   

    public RootContainer() {
        initWidget(uiBinder.createAndBindUi(this));
        errorPanel.add(addErrorPanel("1"));
        errorPanel.add(addErrorPanel("2"));
        errorPanel.add(addErrorPanel("3"));
        errorPanel.add(addErrorPanel("lolololol"));
        errorPanel.add(addErrorPanel("roflroflrofl"));
        errorPanel.add(addErrorPanel("lolololol"));
        errorPanel.add(addErrorPanel("roflroflrofl"));
        errorPanel.add(addErrorPanel("roflroflrofl"));
        errorPanel.add(addErrorPanel("roflroflrofl"));
        errorPanel.add(addErrorPanel("lolololol"));
        errorPanel.add(addErrorPanel("roflroflrofl"));
        errorPanel.add(addErrorPanel("roflroflrofl"));
        
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

    /**
     * Creates an error panel display for the given errorMessage
     * @param String errorMessage to be displayed
     * @return VerticalPanel the error display
     */
    public FlowPanel addErrorPanel(String errorMessage) {
        //Button
        PushButton pushButton = new PushButton(new Image(Resources.INSTANCE.errorMessageClose()));
        pushButton.getElement().setAttribute("style", "float: left");
        pushButton.setSize("8px", "8px");
        pushButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                ((PushButton)event.getSource()).getParent().setVisible(false);
                Widget wi = ((PushButton)event.getSource()).getParent().getParent().getParent();
                System.out.println("Offset: " + wi.getOffsetHeight());
            }
        });
        //Label
        Label label = new Label();
        label.getElement().setAttribute("style", "float: left");
        label.setText(errorMessage);
        //Panel
        FlowPanel flowP = new FlowPanel();
        flowP.add(pushButton);
        flowP.add(label);
        flowP.add(new HTML("<div style=\"clear:both\"/>"));
        return flowP;
    }
}
