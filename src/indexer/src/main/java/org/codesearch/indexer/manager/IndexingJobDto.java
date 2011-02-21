/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.indexer.manager;

import java.util.Date;

/**
 * DTO used to store information needed to display all indexing jobs in the web interface
 * @author David Froehlich
 */
public class IndexingJobDto {
    private String currentType;
    private Date timeStarted;
    private int tasksTotal;
    private int tasksFinished;
    private String currentRepository;

    public String getCurrentRepository() {
        return currentRepository;
    }

    public String getCurrentType() {
        return currentType;
    }

    public int getTasksFinished() {
        return tasksFinished;
    }

    public int getTasksTotal() {
        return tasksTotal;
    }

    public Date getTimeStarted() {
        return timeStarted;
    }

    public IndexingJobDto(String currentType, Date timeStarted, int tasksTotal, int tasksFinished, String currentRepository) {
        this.currentType = currentType;
        this.timeStarted = timeStarted;
        this.tasksTotal = tasksTotal;
        this.tasksFinished = tasksFinished;
        this.currentRepository = currentRepository;
    }
}
