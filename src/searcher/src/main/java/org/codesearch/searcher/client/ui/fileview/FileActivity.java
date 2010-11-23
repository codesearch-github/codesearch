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

/**
 * Presenter for the file view.
 * @author Samuel Kogler
 */
public class FileActivity extends AbstractActivity implements Presenter {

    private ClientFactory clientFactory;
    private FileView fileView;
    private SearcherServiceAsync searcherServiceAsync = GWT.create(SearcherService.class);

   
    public FileActivity(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
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
        panel.setWidget(fileView.asWidget());
        searcherServiceAsync.getFileContent("svn_local", "/home/daasdingo/workspace/svnsearch/doc/gpl", new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                fileView.setFileContent("Could not get file content from the server");
            }

            @Override
            public void onSuccess(String result) {
                    fileView.setFileContent(result);
            }
        });
    }

    /**
     * Navigate to a new place.
     * @param place
     */
    @Override
    public void goTo(Place place) {
        clientFactory.getPlaceController().goTo(place);
    }

}
