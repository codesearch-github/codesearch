package org.codesearch.searcher.client.ui.fileview;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.codesearch.searcher.client.ClientFactory;
import org.codesearch.searcher.client.rpc.SearcherService;
import org.codesearch.searcher.client.rpc.SearcherServiceAsync;
import org.codesearch.searcher.client.ui.fileview.FileView.Presenter;

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


    private class GetFileContentCallback implements AsyncCallback<String>  {

        @Override
        public void onFailure(Throwable caught) {
            Window.alert("Error getting file:\n" + caught.toString());
        }

        @Override
        public void onSuccess(String result) {
            fileContent = result;
            fileView.setFileContent(fileContent);
        }

    }
}
