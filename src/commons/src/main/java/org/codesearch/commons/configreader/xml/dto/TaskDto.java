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
package org.codesearch.commons.configreader.xml.dto;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author David Froehlich
 */
public class TaskDto {
    /** The list of all the repositories this task affects, if it equals null the task will affect all repositories */
    private List<String> repositoryNames;
    /** The type of the task, for instance clear for clearing the entire index, and so on */
    private TaskType type;

    /**
     * Creates a new TaskDto with the given repositoryNames and the type
     * @param repositoryNames the repositories this task affects
     * @param type the type of task
     */
    public TaskDto(List<String> repositoryNames, TaskType type) {
        this.repositoryNames = repositoryNames;
        this.type = type;
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

    /**
     * Indicates a type for the Task, depending on the type a different Task object will be created in the IndexingManager
     */
    public enum TaskType {
        clear, //Clears the entire index
        index //indexes the specified repositories incrementally
    }

    /**
     * Checks whether this task has the same values as the given one
     * @param o the task that is checked
     * @return true if the values are the same
     */
    @Override
    public boolean equals(Object o) {
        TaskDto other;
        try {
            other = (TaskDto) o;
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
