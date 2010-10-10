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
package org.codesearch.indexer.jobs;

import java.util.Date;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.codesearch.commons.configuration.xml.XmlConfigurationReader;
import org.codesearch.commons.configuration.xml.XmlConfigurationReaderConstants;
import org.codesearch.commons.configuration.xml.dto.TaskDto;
import org.codesearch.commons.plugins.Plugin;
import org.codesearch.commons.plugins.PluginLoader;
import org.codesearch.indexer.tasks.IndexingTask;
import org.codesearch.indexer.tasks.Task;
import org.codesearch.indexer.exceptions.TaskExecutionException;
import org.codesearch.indexer.tasks.ClearTask;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * An indexerJob stores one or more tasks and controls their execution
 * @author Stiboller Stephan
 * @author David Froehlich
 */
public class IndexingJob extends QuartzJobBean {

    /** Instantiate a logger */
    private static final Logger LOG = Logger.getLogger(IndexingJob.class);
    /** Indicates if the thread is terminated or not.
     * If flagged as terminated the job will not start the execution of the next task and terminate itself instead */
    private boolean terminated = false;
    /** List of TaskDtos assigned to this IndexingJob */
    private List<TaskDto> taskList;
    /** The XmlConfigReader used to retrieve configuration */
    private XmlConfigurationReader xmlConfigurationReader;
    /** The plugin loader */
    private PluginLoader pluginLoader;

    /**
     * Executes all tasks from the taskList one after another
     * @param jec the JobExecutionContext containing the tasks, the id of the job and whether the job is flagged as terminated or not
     * @throws JobExecutionException if the execution of a task was not successful or if the job was terminated
     */
    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {
        LOG.debug("Executing IndexerJob");
        Task task = null;
        Date startDate = new Date();
        String indexLocation = null;
        try {
            indexLocation = xmlConfigurationReader.getSingleLinePropertyValue(XmlConfigurationReaderConstants.INDEX_LOCATION);
        } catch (ConfigurationException ex) {
            throw new JobExecutionException("Could not read index directory from configuration, job execution failed: " + ex);
        }

        for (int i = 0; i < taskList.size(); i++) {
            TaskDto taskDto = taskList.get(i);
            if (terminated) {
                throw new JobExecutionException("Job was terminated after successful execution of " + i + " of " + taskList.size() + " jobs");
            }
            switch (taskDto.getType()) {
                case index:
                    task = new IndexingTask();
                    ((IndexingTask)task).setPluginLoader(pluginLoader);
                    break;
                case clear:
                    task = new ClearTask();
                    break;
            }
            task.setRepository(taskDto.getRepository());
            task.setIndexLocation(indexLocation);
            try {
                task.execute();
            } catch (TaskExecutionException ex) {
                throw new JobExecutionException("Execution of Task number " + i + " threw an exception: " + ex);
            }
        }

        LOG.debug("Finished execution of job in " + (new Date().getTime() - startDate.getTime()) / 1000f + " seconds");
    }

    public void setXmlConfigurationReader(XmlConfigurationReader xmlConfigurationReader) {
        this.xmlConfigurationReader = xmlConfigurationReader;
    }

    public void setPluginLoader(PluginLoader pluginLoader) {
        this.pluginLoader = pluginLoader;
    }

    public void setTaskList(List<TaskDto> taskList) {
        this.taskList = taskList;
    }

    public void setTerminated(boolean terminated) {
        this.terminated = terminated;
    }
}
