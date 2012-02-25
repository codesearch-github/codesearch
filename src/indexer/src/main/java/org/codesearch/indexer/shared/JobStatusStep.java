package org.codesearch.indexer.shared;

import java.io.Serializable;

/**
 * Represents a step in the processing of a job. Mostly this is used to display
 * the status of indexing a single repository.
 *
 * @author Samuel Kogler
 */
public class JobStatusStep implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * The name or description of the step
     */
    private String name;
    /**
     * The total number of steps in this step.
     * E.g. the total number of files to be indexed.
     */
    private int totalSteps;
    /**
     * The number of already finished steps.
     */
    private int finishedSteps;

    public int getFinishedSteps() {
        return finishedSteps;
    }

    public void setFinishedSteps(int finishedSteps) {
        this.finishedSteps = finishedSteps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }
}
