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
    private String fileContent;

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
    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        fileView = clientFactory.getFileView();
        fileView.setPresenter(this);
        fileView.setFilePath(filePath);
        fileView.setRepository(repository);
        panel.setWidget(fileView.asWidget());
        fileView.setFileContent("loading file...", true);
        searcherServiceAsync.getFileContent(repository, filePath, new GetFileContentCallback());
    }

    /**
     * Navigate to a new place.
     * @param place
     */
    @Override
    public void goTo(Place place) {
        clientFactory.getPlaceController().goTo(place);
    }


    private class GetFileContentCallback implements AsyncCallback<FileDto>  {

        @Override
        public void onFailure(Throwable caught) {
            fileView.setFileContent("Error getting file:\n" + caught.toString(), true);
        }

        @Override
        public void onSuccess(FileDto result) {
            fileContent = result.getFileContent();
            fileView.setFileContent(fileContent, result.isBinary());
        }

    }
}
