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

package org.codesearch.commons.utils.mime;

import java.util.Set;

/**
 * Represents a file type.
 * @author Samuel Kogler
 */
public class FileType {
    private String mime;
    private Set<String> fileEndings;
    private boolean binary;

    public FileType() {
    }

    public FileType(String mime, Set<String> fileEndings, boolean binary) {
        this.mime = mime;
        this.fileEndings = fileEndings;
        this.binary = binary;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FileType other = (FileType) obj;
        if ((this.mime == null) ? (other.mime != null) : !this.mime.equals(other.mime)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (this.mime != null ? this.mime.hashCode() : 0);
        return hash;
    }

    public boolean isBinary() {
        return binary;
    }

    public void setBinary(boolean binary) {
        this.binary = binary;
    }

    public Set<String> getFileEndings() {
        return fileEndings;
    }

    public void setFileEndings(Set<String> fileEndings) {
        this.fileEndings = fileEndings;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }
}
