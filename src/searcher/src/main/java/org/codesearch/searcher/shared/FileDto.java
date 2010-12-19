package org.codesearch.searcher.shared;

import java.io.Serializable;

/**
 * Represents a file.
 * @author Samuel Kogler
 */
public class FileDto implements Serializable {
    String fileContent;
    boolean binary;

    public boolean isBinary() {
        return binary;
    }

    public void setBinary(boolean binary) {
        this.binary = binary;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }
    
}
