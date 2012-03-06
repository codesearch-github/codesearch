package org.codesearch.searcher.shared;

import java.io.Serializable;

/**
 * Represents an location the searcher can jump to.
 * This generally means a file and a line number.
 * @author Samuel Kogler
 */
public class JumpLocation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String filePath;
    private String repository;
    private int lineNumber;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

}
