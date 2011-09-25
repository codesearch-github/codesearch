/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.vcs;

import java.util.List;

import org.codesearch.commons.configuration.dto.RepositoryDto;

/**
 *
 * @author david
 */
public class FileDto {

    private String filePath;
    private String lastAuthor;
    private String lastAlteration;
    private byte[] content;
    private RepositoryDto repository;
    private boolean binary;

    public void setContent(byte[] content) {
        this.content = content;
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

    public RepositoryDto getRepository() {
        return repository;
    }

    public void setRepository(RepositoryDto repository) {
        this.repository = repository;
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

    public byte[] getContent() {
        return content;
    }

    public String getFilePath() {
        return filePath;
    }

    public FileDto(String filePath, String lastAuthor, String lastAlteration, byte[] content, RepositoryDto repository, boolean binary) {
        this.filePath = filePath;
        this.lastAuthor = lastAuthor;
        this.lastAlteration = lastAlteration;
        this.content = content;
        this.repository = repository;
        this.binary = binary;
    }

    public FileDto() {
    }
}
