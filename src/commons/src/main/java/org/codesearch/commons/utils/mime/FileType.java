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
