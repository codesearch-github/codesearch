package org.codesearch.searcher.client.ui.fileview;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View that can display a file's content.
 * @author Samuel Kogler
 */
public interface FileView extends IsWidget {

    public void setFileContent(String fileContent);
    public void setFilePath(String filePath);
    public void setRepository(String repository);
    public void setPresenter(FileView.Presenter presenter);

    public interface Presenter {
        void goTo(Place place);
    }
}
