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
