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
