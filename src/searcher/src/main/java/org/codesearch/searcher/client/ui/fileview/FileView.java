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

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import java.util.List;
import org.codesearch.searcher.shared.OutlineNode;

/**
 * View that can display a file.
 * @author Samuel Kogler
 */
public interface FileView extends IsWidget {

    /**
     * Resets the state of the view.
     */
    void cleanup();

    /**
     * Sets the file content to be displayed.
     * This should be already-highlighted HTML code.
     * @param fileContent The file content
     * @param binary
     */
    void setFileContent(String fileContent, boolean binary);

    /**
     * Sets the path of the file to be displayed.
     * @param filePath The file path
     */
    void setFilePath(String filePath);

    /**
     * Sets the search term that resulted in the current file
     * @param searchTerm
     */
    void setSearchTerm(String searchTerm);

    /**
     * Sets the repository-name of the file to be displayed.
     * @param repository The repository name
     */
    void setRepository(String repository);

    /**
     * Sets the outline to be displayed.
     * @param outline The outline
     */
    void setOutline(List<OutlineNode> outline);

    /**
     * Sets the presenter for this view.
     * @param presenter The presenter
     */
    void setPresenter(FileView.Presenter presenter);

    /**
     * Connects event handlers for hotkeys.
     */
    void connectEventHandlers();

    /**
     * Disconnects event handlers for hotkeys.
     */
    void disconnectEventHandlers();

    /**
     * Ensures that the specified line is shown in the file view
     * and highlights the line.
     * @param lineNumber The specified line
     */
    void goToLine(int lineNumber);

    Presenter getPresenter();

    interface Presenter {
        void goTo(Place place);
        void goToUsage(int usageId);
    }
}
