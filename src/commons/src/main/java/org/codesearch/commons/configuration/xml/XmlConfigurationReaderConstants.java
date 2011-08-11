
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
package org.codesearch.commons.configuration.xml;

//TODO make this non-static and testable.
/**
 * This class includes all constants needed to access the global xml-configuration with the config reader.
 * @author Stephan Stiboller
 */
public final class XmlConfigurationReaderConstants {

    /** Private constructor so its  not instantiable. */
    private XmlConfigurationReaderConstants () {}
    //GLOBAL CONSTANTS
    /** The URL to the searcher web application, used to notify the searcher of changes to the index. */
    public static final String SEARCHER_LOCATION = "searcher-location";
    /** used to store local checkouts/clones of the configured repositories. */
    public static final String VERSION_CONTROL_CACHE_DIR = "version-control-cache-directory";
    /** used to store the data of the users authorized to access the indexer via the web interface */
    public static final String INDEXER_USERS = "indexer-users.indexer-user";
    /** used to access the username of a indexing-user */
    public static final String USERNAME = "username";
    /** Used to access the password of a indexing-user */
    public static final String PASSWORD = "password";
    /** Used to find out the index location */
    public static final String INDEX_LOCATION = "index-location";
    /** Used to access indexing jobs in the xml-config file */
    public static final String INDEX_JOB = "index-jobs.index-job";
    /** Used to access globally blacklisted filenames in the xml-config file */
    public static final String GLOBAL_BLACKLIST = "global-blacklist-filenames.filename";
    /** Used to access globally whitelisted filenames, so filenames that a file hast to match so it is indexed */
    public static final String GLOBAL_WHITELIST = "global-whitelist-filenames.filename";
    /** Used to retrieve all repositories in the xml-config file */
    public static final String REPOSITORY_LIST = "repositories.repository";
    /** Used to retrieve all tasks in the xml-config file */
    @Deprecated
    public static final String TASK_LIST = "tasks.task";
    //REPOSITORY GROUP CONSTANT
    /** Used to retrieve the repository group list */
    public static final String REPOSITORY_GROUP_LIST = "repository-groups";

    //REPOSITORY CONSTANTS
    /** Used to access the repository version control system type parameter */
    public static final String REPOSITORY_VCS = "version-control-system";
    /** Used to access the repository's blacklisted files */
    public static final String REPOSITORY_BLACKLIST = "blacklist-filenames.filename";
    /** Used to access the repository name */
    public static final String REPOSITORY_NAME = "name";
    /** Used to access the repository url */
    public static final String REPOSITORY_URL = "url";
    /** Used to access the username to be used for this repository */
    public static final String REPOSITORY_USERNAME = "username";
    /** Used to access the password to be used for this repository */
    public static final String REPOSITORY_PASSWORD = "password";
    /** Used to find out if code navigation is enabled for a repository */
    public static final String REPOSITORY_CODE_NAVIGATION_ENABLED = "code-navigation-enabled";
    /** Used to find out in which repository group the repository is  */
    public static final String REPOSITORY_GROUPS = "repository-groups.repo-group";
    /** Used to retrieve the filename-patterns files have to match to be indexed */
    public static final String REPOSITORY_WHITELIST_FILENAMES = "whitelist-filenames.filename";
    /** Used to retrieve the used authentication method */
    public static final String REPOSITORY_AUTHENTICATION_DATA = "authentication-data[@type]";
    /** value for type attribute in authentication-data node in case the repository doesn't need authentication */
    public static final String REPOSITORY_AUTHENTICATION_DATA_TYPE_NONE = "none";
    /** value for type attribute in authentication-data node in case the repository supports basic authentication */
    public static final String REPOSITORY_AUTHENTICATION_DATA_TYPE_BASIC = "basic";
    /** value for type attribute in authentication-data node in case the repository uses SSH authentication*/
    public static final String REPOSITORY_AUTHENTICATION_DATA_TYPE_SSH = "ssh";
    /** used to retrieve the username in case the used authentication is 'basic' */
    public static final String REPOSITORY_AUTHENTICATION_DATA_USERNAME = "authentication-data.username";
    /** used to retrieve the password in case the used authentication is 'basic' */
    public static final String REPOSITORY_AUTHENTICATION_DATA_PASSWORD = "authentication-data.password";
    /** used to retrieve the location of the SSH file in case the used authentication is 'SSH' */
    public static final String REPOSITORY_AUTHENTICATION_DATA_SSH_FILE_PATH = "authentication-data.file-path";

    //TASK CONSTANTS
    /** Used to access a single task */
    public static final String TASK = "task";
    /** Used to find out the type of a task */
    public static final String TASK_TYPE = "type";
    /** Used to find out whether an indexing job is set to also add code-navigation specific fields */
    public static final String CODE_NAVIGATION_ENABLED = "code-navigation-enabled";

    //JOB CONSTANTS
    /** Used to access a single job */
    public static final String JOB = "job";
    /** Used to retrieve the cron expression of a job. */
    public static final String JOB_CRON_EXPRESSION = "cron-expression";
    /** Used to retrieve the repositories of a job. */
    public static final String JOB_REPOSITORY = "repositories";
    /** Clear index before indexing? */
    public static final String JOB_CLEAR = "clear";
    
    //DATABASE CONSTANTS
    public static final String DB_USERNAME = "db-username";
    public static final String DB_PASSWORD = "db-password";
    public static final String DB_DRIVER = "db-driver";
    public static final String DB_URL = "db-url";
    public static final String DB_PORT_NUMBER = "db-port-number";
    public static final String DB_NAME = "db-name";
    public static final String DB_MAX_CONNECTIONS = "db-max-connections";
    public static final String DBMS = "dbms";
}
