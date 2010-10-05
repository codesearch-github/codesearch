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

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * DTO storing the information about a single IndexingJob
 * @author David Froehlich
 */
public class JobDto {

    /** A list of all tasks that will be executed in this job */
    private List<TaskDto> tasks;
    /** The original start date for the job, the job will be executed whenever the start date + an arbitrary count of the interval matches the current time
     * for instance startDate is 2010-11-11 (a Saturday) at 6:00 pm, and the interval is 10080 (minutes per week), then the job will be executed every saturday at 6:00 pm
     * if the startDate is 2010-10-13 at 8:00 am and the interval is 60, then the job will be executed every full hour*/
    private Calendar startDate;
    /** The interval in which the job will be executed in minutes */
    private int interval;
    /**
     * Creates a new instance of JobDto
     */
    public JobDto() {
        tasks = new LinkedList<TaskDto>();
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

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public List<TaskDto> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDto> tasks) {
        this.tasks = tasks;
    }


    /**
     * Checks whether this job has the same values as the one given as a parameter
     * @param o the JobDto to check
     * @return true if the JobDtos have the same values
     */
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof JobDto)) {
            return false;
        }
        JobDto other = null;
        try {
            other = (JobDto) o;
            if (other.getInterval() != this.interval) 
                return false;
            
            if (other.getStartDate().getTimeInMillis() != this.startDate.getTimeInMillis()) 
                return false;
            
            if(other.getTasks().size() != this.tasks.size())
                return false;
            for(int i = 0; i < this.tasks.size(); i++){
                TaskDto task = this.tasks.get(i);
                if(!task.equals(other.getTasks().get(i)))
                    return false;
            }
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }
}
