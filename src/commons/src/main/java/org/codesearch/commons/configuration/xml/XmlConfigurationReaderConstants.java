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

/**
 * Copyright 2010 David Froehlich   <david.froehlich@businesssoftware.at>,
 *                Samuel Kogler     <samuel.kogler@gmail.com>,
 *                Stephan Stiboller <stistc06@htlkaindorf.at>
 *
 * This file is part of Codesearch.
 *
 * Codesearch is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General protected License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Codesearch is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General protected License for more details.
 *
 * You should have received a copy of the GNU General protected License
 * along with Codesearch.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.codesearch.commons.configuration.xml;

/**
 * Defines constant values used by the Xml implementation of the configuration reader.
 * The values correspond to xml tags in the config file.
 * @author Samuel Kogler
 * @author Stephan Stiboller
 */
public final class XmlConfigurationReaderConstants {

    /** Private constructor so its  not instantiable. */
    private XmlConfigurationReaderConstants () {}
    //GLOBAL CONSTANTS
    /** The URL to the searcher web application, used to notify the searcher of changes to the index. */
    protected static final String SEARCHER_LOCATION = "searcher-location";
    /** used to store local checkouts/clones of the configured repositories. */
    protected static final String CACHE_DIR = "cache-directory";
    /** used to store the data of the users authorized to access the indexer via the web interface */
    protected static final String INDEXER_USERS = "indexer-users.indexer-user";
    /** used to access the username of a indexing-user */
    protected static final String USERNAME = "username";
    /** Used to access the password of a indexing-user */
    protected static final String PASSWORD = "password";
    /** Used to find out the index location */
    protected static final String INDEX_DIR = "index-directory";
    /** Used to access indexing jobs in the xml-config file */
    protected static final String INDEX_JOB = "index-jobs.index-job";
    /** Used to access globally blacklisted filenames in the xml-config file */
    protected static final String GLOBAL_BLACKLIST = "global-blacklist-filenames.filename";
    /** Used to access globally whitelisted filenames, so filenames that a file hast to match so it is indexed */
    protected static final String GLOBAL_WHITELIST = "global-whitelist-filenames.filename";
    /** Used to retrieve all repositories in the xml-config file */
    protected static final String REPOSITORY_LIST = "repositories.repository";

    //REPOSITORY CONSTANTS
    /** Used to access the repository version control system type parameter */
    protected static final String REPOSITORY_VCS = "version-control-system";
    /** Used to access the repository's blacklisted files */
    protected static final String REPOSITORY_BLACKLIST = "blacklist-filenames.filename";
    /** Used to access the repository name */
    protected static final String REPOSITORY_NAME = "name";
    /** Used to access the repository url */
    protected static final String REPOSITORY_URL = "url";
    /** Used to access the username to be used for this repository */
    protected static final String REPOSITORY_USERNAME = "username";
    /** Used to access the password to be used for this repository */
    protected static final String REPOSITORY_PASSWORD = "password";
    /** Used to find out if code navigation is enabled for a repository */
    protected static final String REPOSITORY_CODE_NAVIGATION_ENABLED = "code-navigation-enabled";
    /** Used to find out in which repository group the repository is  */
    protected static final String REPOSITORY_GROUPS = "groups";
    /** Used to retrieve the filename-patterns files have to match to be indexed */
    protected static final String REPOSITORY_WHITELIST_FILENAMES = "whitelist-filenames.filename";
    /** Used to retrieve the used authentication method */
    protected static final String REPOSITORY_AUTHENTICATION_DATA = "authentication-data[@type]";
    /** value for type attribute in authentication-data node in case the repository doesn't need authentication */
    protected static final String REPOSITORY_AUTHENTICATION_DATA_TYPE_NONE = "none";
    /** value for type attribute in authentication-data node in case the repository supports basic authentication */
    protected static final String REPOSITORY_AUTHENTICATION_DATA_TYPE_BASIC = "basic";
    /** value for type attribute in authentication-data node in case the repository uses SSH authentication*/
    protected static final String REPOSITORY_AUTHENTICATION_DATA_TYPE_SSH = "ssh";
    /** used to retrieve the username in case the used authentication is 'basic' */
    protected static final String REPOSITORY_AUTHENTICATION_DATA_USERNAME = "authentication-data.username";
    /** used to retrieve the password in case the used authentication is 'basic' */
    protected static final String REPOSITORY_AUTHENTICATION_DATA_PASSWORD = "authentication-data.password";
    /** used to retrieve the location of the SSH file in case the used authentication is 'SSH' */
    protected static final String REPOSITORY_AUTHENTICATION_DATA_SSH_FILE_PATH = "authentication-data.file-path";

    //TASK CONSTANTS
    /** Used to access a single task */
    protected static final String TASK = "task";
    /** Used to find out the type of a task */
    protected static final String TASK_TYPE = "type";
    /** Used to find out whether an indexing job is set to also add code-navigation specific fields */
    protected static final String CODE_NAVIGATION_ENABLED = "code-navigation-enabled";

    //JOB CONSTANTS
    /** Used to access a single job */
    protected static final String JOB = "job";
    /** Used to retrieve the cron expression of a job. */
    protected static final String JOB_CRON_EXPRESSION = "cron-expression";
    /** Used to retrieve the repositories of a job. */
    protected static final String JOB_REPOSITORY = "repositories";
    /** Clear index before indexing? */
    protected static final String JOB_CLEAR = "clear";

    //DATABASE CONSTANTS
    protected static final String DB_SECTION = "database";
    protected static final String DB_USERNAME = "username";
    protected static final String DB_PASSWORD = "password";
    protected static final String DB_DRIVER = "driver";
    protected static final String DB_HOSTNAME = "hostname";
    protected static final String DB_PORT_NUMBER = "port-number";
    protected static final String DB_DATABASE = "database-name";
    protected static final String DB_MAX_CONNECTIONS = "max-connections";
    protected static final String DB_PROTOCOL = "protocol";
}
