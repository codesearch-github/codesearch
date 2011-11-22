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

import java.util.List;
import java.util.Set;

import org.codesearch.commons.configuration.dto.RepositoryDto;
import org.codesearch.commons.plugins.Plugin;
import org.codesearch.commons.validator.ValidationException;

/**
 * Base for all version control plugins.
 * @author Samuel Kogler
 */
public interface VersionControlPlugin extends Plugin {
    public final String CURRENT_VERSION = "-1";
    /**
     * Sets the current repository.
     * It is required that the repository is set before calling any of the
     * other Version Control Plugin methods.
     * @param repository
     */
    void setRepository(RepositoryDto repository) throws VersionControlPluginException;

    /**
     * Retrieves the file corresponding to the given file path at the given revision
     * @param filePath The file path relative to the current repository URL
     * @param revision The revision for which the content should be retrieved (use VersionControlPlugin.CURRENT_VERSION for the current version)
     * @return The retrieved file
     */
    FileDto getFileDtoForFileIdentifierAtRevision(FileIdentifier fileInfo, String revision) throws VersionControlPluginException;

    /**
     * Returns a list of changed files since the given revision.
     * @param revision The given revision
     * @return The changed files
     */
    Set<FileIdentifier> getChangedFilesSinceRevision(String revision) throws VersionControlPluginException;

    /**
     * Extracts and returns the revision number of the currently set revision.
     * @return The revision number
     */
    String getRepositoryRevision() throws VersionControlPluginException;

    /**
     * returns a list of the names of all files in the directory (not recursively)
     * @param directoryPath the path of the directory
     * @return the files as a list of Strings
     * @throws VersionControlPluginException
     */
    List<String> getFilesInDirectory(String directoryPath) throws VersionControlPluginException;

    /**
     * Sets the cache directory where the plugin can checkout/store the configured repositories.
     * @param directoryPath The specified directory
     * @throws VersionControlPluginException if the directory is invalid
     */
    void setCacheDirectory(String directoryPath) throws VersionControlPluginException;
    
    /**
     * validates whether the plugin will be usable and a connection to the specified repository can be established
     * aborts all further executions if an exception is thrown
     * @throws ValidationException 
     */
    void validate() throws ValidationException;
}
