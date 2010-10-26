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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import org.codesearch.commons.configuration.xml.XmlConfigurationReader;
import org.codesearch.commons.configuration.xml.dto.TaskDto;
import org.codesearch.indexer.core.IndexerMain;
import org.codesearch.indexer.tasks.IndexingTask;
import org.codesearch.indexer.tasks.Task;
import org.codesearch.indexer.exceptions.TaskExecutionException;
import org.codesearch.indexer.tasks.ClearTask;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

/**
 * An indexerJob stores one or more tasks and controls their execution
 * @author Stiboller Stephan
 * @author David Froehlich
 */
public class IndexerJob implements Job {

    /** Indicates if the thread is terminated or not.
     * If flagged as terminated the job will not start the execution of the next task and terminate itself instead */
    private boolean terminated = false;
    /** List of TaskDtos assigned to this IndexingJob */
    private List<TaskDto> taskList = new LinkedList<TaskDto>();
    /** Instantiate a logger */
    private static final Logger LOG = Logger.getLogger(IndexerJob.class);
    /** The XmlConfigReader used to retrieve configuration */
    private XmlConfigurationReader configReader;

    /**
     * Executes all tasks from the taskList one after another
     * @param jec the JobExecutionContext containing the tasks, the id of the job and whether the job is flagged as terminated or not
     * @throws JobExecutionException if the execution of a task was not successful or if the job was terminated
     */
    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        LOG.info("Executing IndexerJob");
        ApplicationContext applicationContext = IndexerMain.getApplicationContext();
        terminated = (Boolean) jec.getJobDetail().getJobDataMap().get("terminated");
        configReader = new XmlConfigurationReader(); //TODO replace with spring injection
        taskList = (List<TaskDto>) (jec.getJobDetail().getJobDataMap().get("tasks"));
        Task task = null;
        Date startDate = new Date();
        for (int i = 0; i < taskList.size(); i++) {
            TaskDto taskDto = taskList.get(i);
            if (terminated) {
                throw new JobExecutionException("Job was terminated after successful execution of " + i + " of " + taskList.size() + " jobs");
            }
            switch (taskDto.getType()) {
                case index: {
                    task = (Task) applicationContext.getBean("indexingTask");
                    ((IndexingTask) task).setRepository(taskDto.getRepository());
                    break;
                }
                case clear: {
                    task = new ClearTask();
                    ((ClearTask) task).setRepositoryName(taskDto.getRepository().getName());
                    break;
                } case codeAnalysis: {
                    task = (Task) applicationContext.getBean("indexingTask"); //TODO add bean with correct setting for codeAnalysisEnabled
                    ((IndexingTask) task).setRepository(taskDto.getRepository());
                    ((IndexingTask) task).setCodeAnalysisEnabled(true);
                }
            }
            try {
                task.execute();
            } catch (TaskExecutionException ex) {
                throw new JobExecutionException("Execution of Task number " + i + " threw an exception" + ex);
            }
        }
        LOG.debug("Finished execution of job in " + (new Date().getTime() - startDate.getTime()) / 1000f + " seconds");
    }
}
