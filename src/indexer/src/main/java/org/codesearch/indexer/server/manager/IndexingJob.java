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
package org.codesearch.indexer.server.manager;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.configuration.dto.RepositoryDto;
import org.codesearch.commons.database.DBAccess;
import org.codesearch.commons.plugins.PluginLoader;
import org.codesearch.indexer.server.exceptions.TaskExecutionException;
import org.codesearch.indexer.server.tasks.ClearTask;
import org.codesearch.indexer.server.tasks.IndexingTask;
import org.codesearch.indexer.server.tasks.Task;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.inject.Inject;

/**
 * Stores one or more tasks and controls their execution.
 * 
 * @author Stiboller Stephan
 * @author David Froehlich
 * @author Samuel Kogler
 */
public class IndexingJob implements Job {

    public static final String FIELD_REPOSITORIES = "repositories";
    public static final String FIELD_TERMINATED = "terminated";
    public static final String FIELD_CLEAR_INDEX = "clear_index";
    public static final String FIELD_CURRENT_REPOSITORY = "current_repository";
    public static final String FIELD_FINISHED_REPOSITORIES = "finished_repositories";
    public static final String FIELD_STATUS = "status";

    public static final String STATUS_CLEARING = "clearing";
    public static final String STATUS_INDEXING = "indexing";

    public static final String GROUP_NAME = "INDEXING_JOBS";
    public static final String TRIGGER_GROUP_NAME = "INDEXING_JOB_TRIGGER";

    /** Instantiate a logger */
    private static final Logger LOG = Logger.getLogger(IndexingJob.class);
    /** The config reader used to read the configuration */
    private ConfigurationReader configReader;
    /** The database access object */
    private DBAccess dba;
    /** The plugin loader. */
    private PluginLoader pluginLoader;
    /** The affected repositories. */
    private List<RepositoryDto> repositories;
    /**
     * whether or not the index should be cleared for the specified repositories before indexing
     */
    private boolean clearIndex;

    /** The location of the index. */
    private File indexLocation;

    /** The current job data map of the job. */
    private JobDataMap jobDataMap;

    @Inject
    public IndexingJob(ConfigurationReader configReader, DBAccess dba, PluginLoader pluginLoader) {
        this.configReader = configReader;
        this.dba = dba;
        this.pluginLoader = pluginLoader;
        indexLocation = configReader.getIndexLocation();
    }

    /**
     * Executes all tasks from the taskList one after another
     * 
     * @param jec the JobExecutionContext containing the tasks, the id of the job and whether the job is flagged as terminated or not
     * @throws JobExecutionException if the execution of a task was not successful or if the job was terminated
     */
    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        jobDataMap = jec.getJobDetail().getJobDataMap();
        jobDataMap.put(FIELD_CURRENT_REPOSITORY, "");
        jobDataMap.put(FIELD_FINISHED_REPOSITORIES, 0);
        repositories = (List<RepositoryDto>) jobDataMap.get(FIELD_REPOSITORIES);
        clearIndex = (Boolean) jobDataMap.get(FIELD_CLEAR_INDEX);
        LOG.info("Executing " + jec.getJobDetail().getKey().toString() + ", indexing " + repositories.size() + " repositories");

        Date startDate = new Date();

        try {
            if (clearIndex) {
                jobDataMap.put(FIELD_STATUS, STATUS_CLEARING);
                // clear the index of data associated to the specified
                // repositories
                ClearTask clearTask = new ClearTask(dba);
                clearTask.setRepositories(repositories);
                clearTask.setIndexLocation(indexLocation);
                clearTask.setJob(this);
                clearTask.execute();
            }

            jobDataMap.put(FIELD_CURRENT_REPOSITORY, "");
            jobDataMap.put(FIELD_FINISHED_REPOSITORIES, 0);
            jobDataMap.put(FIELD_STATUS, STATUS_INDEXING);
            // execution of regular indexing job
            Task indexingTask = new IndexingTask(dba, pluginLoader, configReader.getSearcherLocation());
            indexingTask.setRepositories(repositories);
            indexingTask.setIndexLocation(configReader.getIndexLocation());
            indexingTask.setJob(this);
            indexingTask.execute();
        } catch (TaskExecutionException ex) {
            throw new JobExecutionException("Execution of IndexingJob threw an exception" + ex);
        }

        LOG.debug("Finished execution of job in " + (new Date().getTime() - startDate.getTime()) / 1000f + " seconds");
        LOG.info("Finished execution of " + jec.getJobDetail().getKey().toString());
    }

    public void setCurrentRepository(int index) {
        jobDataMap.put(FIELD_FINISHED_REPOSITORIES, index + 1);
        jobDataMap.put(FIELD_CURRENT_REPOSITORY, repositories.get(index).getName());
    }
}
