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
package org.codesearch.indexer.client.ui.log;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class LogViewImpl extends Composite implements LogView {

    private static IndexViewImplUiBinder uiBinder = GWT.create(IndexViewImplUiBinder.class);

    private Presenter presenter;

    @UiField
    FlexTable logTable;
    @UiField
    Button refreshButton;

    interface IndexViewImplUiBinder extends UiBinder<Widget, LogViewImpl> {
    }

    public LogViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("refreshButton")
    void onRefresh(ClickEvent evt) {
        presenter.refresh();
    }

    @Override
    public void cleanup() {
        logTable.clear();
        logTable.removeAllRows();
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void connectEventHandlers() {
        // TODO Auto-generated method stub

    }

    @Override
    public void disconnectEventHandlers() {
        // TODO Auto-generated method stub

    }

    @Override
    public Presenter getPresenter() {
        return presenter;
    }

    /** {@inheritDoc} */
    @Override
    public void setLog(List<String> log) {
        int index = 0;
        for (String message : log) {
            logTable.setText(index, 0, message);
            index++;
        }
    }

}
