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

package org.codesearch.commons.plugins.vcs;

import java.util.Set;

/**
 * Base for all version control plugins.
 * @author Samuel Kogler
 */

public interface VersionControlPlugin {

    /**
     * Sets the current repository base URL.
     * It is required that the URL is set before calling any of the
     * other Version Control Plugin methods.
     * @param url The URL
     */
    void setRepositoryURL(String url);

    /**
     * Retrieves the file content for the given file path.
     * @param filePath The file path relative to the current repository URL
     * @return The retrieved file content
     */
    String getFileContentForFilePath(String filePath);

    /**
     * Returns a list of changed file paths since the given revision.
     * @param revision The given revision
     * @return The paths of the changed files
     */
    Set<String> getPathsForChangedFilesSinceRevision(String revision);
}
