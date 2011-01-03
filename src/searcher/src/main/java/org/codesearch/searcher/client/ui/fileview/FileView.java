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

/**
 * View that can display a file.
 * @author Samuel Kogler
 */
public interface FileView extends IsWidget {

    /**
     * Sets the file content to be displayed.
     * This should be already-highlighted HTML code.
     * @param fileContent The file content
     * @param binary
     */
    public void setFileContent(String fileContent, boolean binary);

    /**
     * Sets the path of the file to be displayed.
     * @param filePath The file path
     */
    public void setFilePath(String filePath);

    /**
     * Sets the repository-name of the file to be displayed.
     * @param repository The repository name
     */
    public void setRepository(String repository);

    /**
     * Sets the presenter for this view.
     * @param presenter The presenter
     */
    public void setPresenter(FileView.Presenter presenter);
    
    public void connectEventHandlers();

    public void disconnectEventHandlers();
    public interface Presenter {
        void goTo(Place place);
    }
}
