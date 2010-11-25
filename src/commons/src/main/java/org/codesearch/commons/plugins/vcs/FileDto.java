/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.vcs;

import java.io.ByteArrayOutputStream;

/**
 * stores information about a specific file in a repository
 * @author David Froehlich
 */
public class FileDto {
    private String filePath;
    private ByteArrayOutputStream content;
    /** determines whether the file is binary */
    private boolean binary;

    public ByteArrayOutputStream getContent() {
        return content;
    }

    public void setContent(ByteArrayOutputStream content) {
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

    public FileDto(String filePath, ByteArrayOutputStream content, boolean binary) {
        this.filePath = filePath;
        this.content = content;
        this.binary = binary;
    }
}
