/**
 * Copyright 2010 David Froehlich <david.froehlich@businesssoftware.at>, Samuel
 * Kogler <samuel.kogler@gmail.com>, Stephan Stiboller <stistc06@htlkaindorf.at>
 *
 * This file is part of Codesearch.
 *
 * Codesearch is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Codesearch is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Codesearch. If not, see <http://www.gnu.org/licenses/>.
 */
package org.codesearch.indexer.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Samuel Kogler Represents the status of a configured Job.
 */
public class JobStatus implements Serializable {

    /**
     * .
     */
    private static final long serialVersionUID = -4303740745569729444L;
    /**
     * The name of the job.
     */
    private String name;
    /**
     * The start date, can be in the future for scheduled jobs or in the past
     * for finished or failed jobs.
     */
    private Date start;
    /**
     * The date when the job was finished, null if not yet finished.
     */
    private Date finished;
    /**
     * The name of the repository currently being processed.
     */
    private String currentRepository;
    /**
     * The repositories configured for the job.
     */
    private List<String> repositories;
    /**
     * The number of currently finished repositories.
     */
    private int finishedRepositories;

    /*
     * Contains more detail about the currently running process.
     */
    private JobStatusStep currentStep = new JobStatusStep();

    public JobStatus() {
        currentStep.setName("UNKNOWN");
    }

    public List<String> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<String> repositories) {
        this.repositories = repositories;
    }

    public JobStatusStep getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(JobStatusStep currentStep) {
        this.currentStep = currentStep;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date started) {
        this.start = started;
    }

    public Date getFinished() {
        return finished;
    }

    public void setFinished(Date finished) {
        this.finished = finished;
    }

    public int getFinishedRepositories() {
        return finishedRepositories;
    }

    public void setFinishedRepositories(int finishedRepositories) {
        this.finishedRepositories = finishedRepositories;
    }

    public String getCurrentRepository() {
        return currentRepository;
    }

    public void setCurrentRepository(String currentRepository) {
        this.currentRepository = currentRepository;
    }
}
