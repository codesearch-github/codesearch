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
package org.codesearch.indexer.shared;

import java.io.Serializable;

/**
 * Represents the indexing-status of a repository
 * @author David Froehlich
 */
public class RepositoryStatus implements Serializable {

    /** . */
    private static final long serialVersionUID = -4238507594081282969L;
    
    private String repositoryName;
    private String revision;
    private Status status;

    public RepositoryStatus() {
    }

    public enum Status {

        INDEXED, // if everything is alright
        INCONSISTENT, // if an indexing process failed
        EMPTY //if the repository has never been indexed
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public RepositoryStatus(String repositoryName, String revision, Status status) {
        this.repositoryName = repositoryName;
        this.revision = revision;
        this.status = status;
    }
}
