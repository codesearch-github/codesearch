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
package org.codesearch.commons.configuration.xml.dto;

import java.util.LinkedList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;

/**
 * Represents an Indexing Job.
 * @author David Froehlich
 */
public class JobDto {

    /** A list of all tasks that will be executed in this job */
    private List<TaskDto> tasks;
    /** The cron expression for the job. */
    private String cronExpression;

    public JobDto() {
        tasks = new LinkedList<TaskDto>();
    }

    public List<TaskDto> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDto> tasks) {
        this.tasks = tasks;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JobDto)) {
            return false;
        }

        JobDto other = (JobDto) o;

        if (cronExpression != null) {
            if (!cronExpression.equals(other.getCronExpression())) {
                return false;
            }
        }

        if (tasks.size() == other.getTasks().size()) {
            for (int i = 0; i < tasks.size(); i++) {
                if (!tasks.get(i).equals(other.getTasks().get(i))) {
                    return false;
                }
            }
        } else {
            return false;
        }

        return true;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + (this.tasks != null ? this.tasks.hashCode() : 0);
        hash = 13 * hash + (this.cronExpression != null ? this.cronExpression.hashCode() : 0);
        return hash;
    }
}
