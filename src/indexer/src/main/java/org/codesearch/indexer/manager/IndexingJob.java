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
package org.codesearch.indexer.manager;

import com.google.inject.Inject;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.configuration.xml.XmlConfigurationReaderConstants;
import org.codesearch.commons.configuration.xml.dto.IndexingTaskType;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.database.DBAccess;
import org.codesearch.commons.plugins.PluginLoader;
import org.codesearch.indexer.exceptions.TaskExecutionException;
import org.codesearch.indexer.tasks.ClearTask;
import org.codesearch.indexer.tasks.IndexingTask;
import org.codesearch.indexer.tasks.Task;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Stores one or more tasks and controls their execution.
 * @author Stiboller Stephan
 * @author David Froehlich
 * @author Samuel Kogler
 */
public class IndexingJob implements Job {

    public static final String FIELD_ID = "id";
    public static final String FIELD_TASKS = "tasks";
    public static final String FIELD_REPOSITORIES = "repositories";
    public static final String FIELD_TERMINATED = "terminated";
    public static final String FIELD_CLEAR_INDEX = "clear_index";
    public static final String FIELD_TASKS_FINISHED = "tasks_finished";
    @Deprecated
    public static final String FIELD_CURRENT_TYPE = "type";
    public static final String FIELD_CURRENT_REPOSITORY = "current_repository";
    public static final String INDEXING_JOB_GROUP_NAME = "INDEXING_JOBS";
    public static final String INDEXING_JOB_TRIGGER_GROUP_NAME = "INDEXING_JOB_TRIGGER";
    /** Instantiate a logger */
    private static final Logger LOG = Logger.getLogger(IndexingJob.class);
    /** Indicates whether the thread is terminated or not.
     * If flagged as terminated the job will not start the execution of the next task and terminate itself instead */
    @Deprecated
    private boolean terminated = false;
    /** The config reader used to read the configuration */
    private ConfigurationReader configReader;
    /** The database access object */
    private DBAccess dba;
    /** The plugin loader. */
    private PluginLoader pluginLoader;
    /** The affected repositories. */
    private List<RepositoryDto> repositories;
    /** whether or not the index should be cleared for the specified repositories before indexing */
    private boolean clearIndex;

    @Inject
    public IndexingJob(ConfigurationReader configReader, DBAccess dba, PluginLoader pluginLoader) {
        this.configReader = configReader;
        this.dba = dba;
        this.pluginLoader = pluginLoader;
    }

    /**
     * Executes all tasks from the taskList one after another
     * @param jec the JobExecutionContext containing the tasks, the id of the job and whether the job is flagged as terminated or not
     * @throws JobExecutionException if the execution of a task was not successful or if the job was terminated
     */
    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        repositories = (List<RepositoryDto>) (jec.getJobDetail().getJobDataMap().get(FIELD_REPOSITORIES));
        clearIndex = (Boolean) (jec.getJobDetail().getJobDataMap().get(FIELD_CLEAR_INDEX));
        LOG.info("Executing IndexingJob " + jec.getJobDetail().getKey().getName() + " indexing " + repositories.size() + " repositories");

        Date startDate = new Date();

        try {
            if (clearIndex) {
                LOG.info("Clearing index for specified repositories");
                //clear the index of data associated to the specified repositories
                Task clearTask = new ClearTask(dba);
                clearTask.setRepositories(repositories);
                clearTask.setIndexLocation(configReader.getValue(XmlConfigurationReaderConstants.INDEX_LOCATION));
                clearTask.execute();
            }

            //execution of regular indexing job

            Task indexingTask = new IndexingTask(dba, pluginLoader, configReader.getValue(XmlConfigurationReaderConstants.SEARCHER_LOCATION));
            indexingTask.setRepositories(repositories);
            indexingTask.setIndexLocation(configReader.getValue(XmlConfigurationReaderConstants.INDEX_LOCATION));
            indexingTask.execute();
        } catch (TaskExecutionException ex) {
            throw new JobExecutionException("Execution of IndexingJob threw an exception" + ex);
        }

        LOG.debug("Finished execution of job in " + (new Date().getTime() - startDate.getTime()) / 1000f + " seconds");
        LOG.info("Finished execution of IndexingJob " + jec.getJobDetail().getKey().getName());
    }

    public List<RepositoryDto> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<RepositoryDto> repositories) {
        this.repositories = repositories;
    }

    public void setClear(boolean clearIndex) {
        this.clearIndex = clearIndex;
    }
}
