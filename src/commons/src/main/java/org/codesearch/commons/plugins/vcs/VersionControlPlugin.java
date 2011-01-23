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

import java.net.URI;
import java.util.List;
import java.util.Set;
import org.codesearch.commons.plugins.Plugin;

/**
 * Base for all version control plugins.
 * @author Samuel Kogler
 */
public interface VersionControlPlugin extends Plugin {

    /**
     * Sets the current repository.
     * It is required that the repository is set before calling any of the
     * other Version Control Plugin methods.
     * @param url The repository base URL.
     * @param username The username used to access the repository.
     * @param password The password used to access the repository.
     */
    void setRepository(URI url, String username, String password) throws VersionControlPluginException;

    /**
     * Retrieves the file corresponding to the given file path.
     * @param filePath The file path relative to the current repository URL
     * @return The retrieved file
     */
    FileDto getFileForFilePath(String filePath) throws VersionControlPluginException;

    /**
     * Returns a list of changed files since the given revision.
     * @param revision The given revision
     * @return The changed files
     */
    Set<FileDto> getChangedFilesSinceRevision(String revision) throws VersionControlPluginException;

    /**
     * Extracts and returns the revision number of the currently set revision.
     * @return The revision number
     */
    String getRepositoryRevision() throws VersionControlPluginException;

    /**
     * returns a list of all files in the directory (not recursively)
     * @param directoryPath the path of the directory
     * @return the files as a list of Strings
     * @throws VersionControlPluginException
     */
    List<String> getFilesInDirectory(String directoryPath) throws VersionControlPluginException;
}
