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
package org.codesearch.commons.plugins.vcs;

import org.codesearch.commons.configuration.dto.RepositoryDto;

/**
 * stores information about a specific file in a repository
 * does not contain the actual content of the file
 * the content can be retrieved by using the corresponding vcs plugin to get the FileDto object
 * @author David Froehlich
 */
public class FileIdentifier {

    private String filePath;
    /** determines whether the file is binary */
    private boolean binary;
    private boolean deleted;
    private RepositoryDto repository;

    public FileIdentifier() {
    }

    public FileIdentifier(String filePath, boolean binary, boolean deleted, RepositoryDto repository) {
        this.filePath = filePath;
        this.binary = binary;
        this.deleted = deleted;
        this.repository = repository;
    }

    public RepositoryDto getRepository(){
        return repository;
    }

    /** determines if the file has been deleted since the last indexing process
     * is handed to the IndexerTask so all database entries and lucene fields associated to the file get purged
     */
    public boolean isDeleted() {
        return deleted;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean isBinary() {
        return binary;
    }

    public void setBinary(boolean binary) {
        this.binary = binary;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setRepository(RepositoryDto repository) {
        this.repository = repository;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FileIdentifier) {
            return o.hashCode() == this.hashCode();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 201 + (this.filePath != null ? this.filePath.hashCode() : 0);
    }
}
