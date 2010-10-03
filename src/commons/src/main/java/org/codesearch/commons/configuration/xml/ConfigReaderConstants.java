/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.configuration.xml;

/**
 * This class includes all constants needed to access the global xml-configuration file.
 * @author zeheron
 */
public class ConfigReaderConstants {

    //GLOBAL CONSTANTS
    /** Used to access indexing jobs in the xml-config file */
    public static final String INDEX_JOB = "index-jobs.index-job";
    /** Used to access globally blacklisted filenames in the xml-config file */
    public static final String GLOBAL_BLACKLIST = "global-blacklist-filenames.filename";
    /** Used to retrieve all repositories in the xml-config file */
    public static final String REPOSITORY_LIST = "repositories.repository-group.repository";
    /** Used to retrieve all tasks in the xml-config file */
    public static final String TASK_LIST = "tasks.task";

    //REPOSITORY GROUP CONSTANT
    /** Used to retrieve the repository group list */
    public static final String REPOSITORY_GROUP_LIST = "repositories.repository-groups";

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
    public static final String REPOSITORY_GROUP = "repo-group";

    //TASK CONSTANTS
    /** Used to access a single task */
    public static final String TASK = "task";
    /** Used to find out the type of a task */
    public static final String TASK_TYPE = "type";
    
    //JOB CONSTANTS
    /** Used to access a single job */
    public static final String JOB = "job";
    /** Used to retrieve the interval value of the task*/
    public static final String JOB_INTERVAL = "interval";
    /** Used to retrieve the start date of the task */
    public static final String JOB_START_DATE = "start";
    /** Used to retrieve the repositories of the task */
    public static final String JOB_REPOSITORY = "repositories";

}
