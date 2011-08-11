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

import java.util.List;

/**
 * Represents an Indexing Job.
 * @author David Froehlich
 */
public class JobDto {

    /** The affected repositories. */
    private List<RepositoryDto> repositories;
    /** The cron expression for the job. */
    private String cronExpression;
    /** whether the index will be cleared before indexing */
    private boolean clearIndex;

    public JobDto() {
    }

    public List<RepositoryDto> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<RepositoryDto> repositories) {
        this.repositories = repositories;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public boolean isClearIndex() {
        return clearIndex;
    }

    public void setClearIndex(boolean clearIndex) {
        this.clearIndex = clearIndex;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JobDto other = (JobDto) obj;
        if (this.clearIndex != other.clearIndex) {
            return false;
        }
        if ((this.cronExpression == null) ? (other.cronExpression != null) : !this.cronExpression.equals(other.cronExpression)) {
            return false;
        }
        if (this.repositories != other.repositories && (this.repositories == null || !this.repositories.equals(other.repositories))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.repositories != null ? this.repositories.hashCode() : 0);
        hash = 37 * hash + (this.cronExpression != null ? this.cronExpression.hashCode() : 0);
        return hash;
    }
}
