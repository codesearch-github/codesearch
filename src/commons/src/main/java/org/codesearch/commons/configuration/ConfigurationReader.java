/**
 * Copyright 2010 David Froehlich <david.froehlich@businesssoftware.at>, Samuel Kogler <samuel.kogler@gmail.com>, Stephan Stiboller
 * <stistc06@htlkaindorf.at>
 *
 * This file is part of Codesearch.
 *
 * Codesearch is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Codesearch is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Codesearch. If not, see <http://www.gnu.org/licenses/>.
 */

package org.codesearch.commons.configuration;

import java.io.File;
import java.net.URI;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.codesearch.commons.configuration.dto.DatabaseConfiguration;
import org.codesearch.commons.configuration.dto.IndexerUserDto;
import org.codesearch.commons.configuration.dto.JobDto;
import org.codesearch.commons.configuration.dto.RepositoryDto;

/**
 * Provides access to the program configuration.
 *
 * @author Samuel Kogler
 */
public interface ConfigurationReader {

    /**
     * returns all users authorized to access the indexer via the web interface
     *
     * @return the users as a list of IndexerUserDto
     */
    List<IndexerUserDto> getIndexerUsers();

    /**
     * Retrieves a list of all indexer_jobs from the configuration and returns it as a list of JobDto
     *
     * @return The list of JobDtos
     */
    List<JobDto> getJobs();

    /**
     * Returns a list of repositories defined in the configuration. Checks if the XMLConfiguration is instantiated and, if not, instantiates
     * it.
     *
     * @return the list of all repositories
     * @throws ConfigurationException If the config file is either not found or contains invalid data.
     */
    List<RepositoryDto> getRepositories();

    /**
     * returns a list of the names of all repositories that are in the given group
     *
     * @param groupName the name of the group
     * @return the list of the repositories
     */
    List<String> getRepositoriesForGroup(String groupName);

    /**
     * Returns the repository dto with the given name
     *
     * @param name the name of the repository
     * @return the dto of the repository or null if none was found
     * @throws ConfigurationException if the configuration could not be loaded
     */
    RepositoryDto getRepositoryByName(String name);

    /**
     * Retrieves all existing Repository groups
     *
     * @return list of all repo groups
     */
    List<String> getRepositoryGroups();

    /**
     * Retrieves the configuration data for the database connection.
     *
     * @return The configuration
     */
    DatabaseConfiguration getDatabaseConfiguration();

    /**
     * Returns the path to the directory that is used to store version control data like checked out repositories.
     *
     * @return The path to the cache directory
     */
    File getCacheDirectory();

    /**
     * Returns the path to the directory that is used to store the index.
     * @return The path to the index directory
     */
    File getIndexLocation();

    /**
     * Returns the URI of the searcher.
     * @return The URI of the searcher
     */
    URI getSearcherLocation();

    /**
     * Reloads the configuration to reflect changes.
     */
    void refresh();
}
