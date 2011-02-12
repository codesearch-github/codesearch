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

import java.util.List;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;

/**
 * stores information about a specific file in a repository
 * @author David Froehlich
 */
public class FileDto {

    private String filePath;
    private byte[] content;
    /** determines whether the file is binary */
    private boolean binary;
    private String lastAuthor;
    private String lastAlteration;
    private List<String> imports;

    private RepositoryDto repository;

    public RepositoryDto getRepository() {
        return repository;
    }

    public void setRepository(RepositoryDto repository) {
        this.repository = repository;
    }
    /** determines if the file has been deleted since the last indexing process
     * is handed to the IndexerTask so all database entries and lucene fields associated to the file get purged
     */
    private boolean deleted;

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
    public String getLastAlteration() {
        return lastAlteration;
    }

    public void setLastAlteration(String lastAlteration) {
        this.lastAlteration = lastAlteration;
    }

    public String getLastAuthor() {
        return lastAuthor;
    }

    public void setLastAuthor(String lastAuthor) {
        this.lastAuthor = lastAuthor;
    }

    public List<String> getImports() {
        return imports;
    }

    public void setImports(List<String> imports) {
        this.imports = imports;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isBinary() {
        return binary;
    }

    public void setBinary(boolean binary) {
        this.binary = binary;
    }

    public FileDto() {
    }

    public FileDto(String filePath, byte[] content, boolean binary) {
        this.filePath = filePath;
        this.content = content;
        this.binary = binary;
    }
}
