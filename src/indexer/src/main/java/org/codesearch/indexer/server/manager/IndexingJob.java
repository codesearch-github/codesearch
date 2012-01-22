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
import java.io.IOException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.codesearch.commons.configuration.properties.PropertiesManager;
import org.codesearch.commons.plugins.lucenefields.LuceneFieldPluginLoader;

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
    /** The lucene field plugin loader. */
    private LuceneFieldPluginLoader luceneFieldPluginLoader;
    /** The affected repositories. */
    private List<RepositoryDto> repositories;
    private PropertiesManager propertiesManager;

    /**
     * whether or not the index should be cleared for the specified repositories before indexing
     */
    private boolean clearIndex;
    /** The location of the index. */
    private File indexLocation;
    /** The current job data map of the job. */
    private JobDataMap jobDataMap;

    @Inject
    public IndexingJob(ConfigurationReader configReader, DBAccess dba, PluginLoader pluginLoader, LuceneFieldPluginLoader luceneFieldPluginLoader, PropertiesManager propertiesManager) {
        this.configReader = configReader;
        this.dba = dba;
        this.propertiesManager = propertiesManager;
        this.pluginLoader = pluginLoader;
        this.luceneFieldPluginLoader = luceneFieldPluginLoader;
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
                ClearTask clearTask = new ClearTask(dba, propertiesManager, repositories, indexLocation, this);
                clearTask.execute();
            }

            jobDataMap.put(FIELD_CURRENT_REPOSITORY, "");
            jobDataMap.put(FIELD_FINISHED_REPOSITORIES, 0);
            jobDataMap.put(FIELD_STATUS, STATUS_INDEXING);
            // execution of regular indexing job
            // By default, fields are indexed case insensitive
        
            Directory indexDirectory = FSDirectory.open(indexLocation);
            LOG.info("Opened index at " + indexDirectory);
            Task indexingTask = new IndexingTask(dba, pluginLoader, configReader.getSearcherLocation(), luceneFieldPluginLoader, propertiesManager, repositories, indexDirectory, this);
            indexingTask.execute();
        } catch (TaskExecutionException ex) {
            String errorMsg = "Execution of IndexingJob threw an exception:\n" + ex;
            LOG.error(errorMsg);
            //logging + throwing because of quartz
            throw new JobExecutionException(errorMsg);
        }catch (IOException ex) {
            String errorMsg = "Cannot access index directory at: " + indexLocation;
            LOG.error(errorMsg);
            throw new JobExecutionException(errorMsg);
        }

        LOG.debug("Finished execution of job in " + (new Date().getTime() - startDate.getTime()) / 1000f + " seconds");
        LOG.info("Finished execution of " + jec.getJobDetail().getKey().toString());
    }

    public void setCurrentRepository(int index) {
        jobDataMap.put(FIELD_FINISHED_REPOSITORIES, index + 1);
        jobDataMap.put(FIELD_CURRENT_REPOSITORY, repositories.get(index).getName());
    }
}
