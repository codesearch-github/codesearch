/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.configreader.xml.dto;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author David Froehlich
 */
public class TaskDto {

    private List<String> repositoryNames;
    private TaskType type;
    private int interval;
    private Calendar startDate;

    public TaskDto(List<String> repositoryNames, TaskType type, int interval, Calendar startDate) {
        this.repositoryNames = repositoryNames;
        this.type = type;
        this.interval = interval;
        this.startDate = startDate;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Date Calendar) {
        this.startDate = startDate;
    }

    public List<String> getRepositoryNames() {
        return repositoryNames;
    }

    public void setRepositoryNames(LinkedList<String> repositoryNames) {
        this.repositoryNames = repositoryNames;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public enum TaskType {

        clear,
        index
    }

    @Override
    public boolean equals(Object o) {
        TaskDto other;
        try {
            other = (TaskDto) o;
            if (other.getInterval() != this.interval) {
                return false;
            }
            if (other.getStartDate().getTimeInMillis() != this.startDate.getTimeInMillis()) {
                return false;
            }
            if (!(this.repositoryNames == null && other.getRepositoryNames() == null)) {
                for (int i = 0; i < other.getRepositoryNames().size(); i++) {
                    if (!(other.getRepositoryNames().get(i).equals(this.getRepositoryNames().get(i)))) {
                        return false;
                    }
                }
                if (this.repositoryNames.size() != other.repositoryNames.size()) {
                    return false;
                }
            }
            if (this.type == other.getType()) {
                return true;
            }
        } catch (ClassCastException e) {
            return false;
        }
        return false;
    }
}
