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

package org.codesearch.searcher.client.ui.fileview;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.codesearch.searcher.client.ClientFactory;
import org.codesearch.searcher.client.rpc.SearcherService;
import org.codesearch.searcher.client.rpc.SearcherServiceAsync;
import org.codesearch.searcher.client.ui.fileview.FileView.Presenter;
import org.codesearch.searcher.shared.FileDto;

/**
 * Presenter for the file view.
 * @author Samuel Kogler
 */
public class FileActivity extends AbstractActivity implements Presenter {

    private ClientFactory clientFactory;
    private FileView fileView;
    private SearcherServiceAsync searcherServiceAsync = GWT.create(SearcherService.class);
    private String repository;
    private String filePath;

    public FileActivity(ClientFactory clientFactory, String repository, String filePath) {
        this.clientFactory = clientFactory;
        this.repository = repository;
        this.filePath = filePath;
    }

    /**
     * Starts the activity.
     * @param panel
     * @param eventBus
     */
    /** {@inheritDoc} */
    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        fileView = clientFactory.getFileView();
        fileView.setPresenter(this);
        fileView.cleanup();
        fileView.setFilePath(filePath);
        fileView.setRepository(repository);
        fileView.connectEventHandlers();
        panel.setWidget(fileView.asWidget());
        fileView.setFileContent("loading file...", true);
        searcherServiceAsync.getFile(repository, filePath, new GetFileCallback());
    }

    /**
     * Navigate to a new place.
     * @param place
     */
    /** {@inheritDoc} */
    @Override
    public void goTo(Place place) {
        fileView.disconnectEventHandlers();
        clientFactory.getPlaceController().goTo(place);
    }

    private class GetFileCallback implements AsyncCallback<FileDto>  {

        /** {@inheritDoc} */
        @Override
        public void onFailure(Throwable caught) {
            fileView.setFileContent("Error getting file:\n" + caught.toString(), true);
        }

        /** {@inheritDoc} */
        @Override
        public void onSuccess(FileDto result) {
            fileView.cleanup();
            fileView.setFileContent(result.getFileContent(), result.isBinary());
            fileView.setOutline(result.getOutline());
        }

    }
}
