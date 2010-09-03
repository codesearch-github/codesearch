/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.searcher.shared;

/**
 *
 * @author David Froehlich
 */
public class ResultItem {
    private String filePath;
    private String repository;
    private float relevance;

    public ResultItem(String filePath, String repository, float relevance) {
        this.filePath = filePath;
        this.repository = repository;
        this.relevance = relevance;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public float getRelevance() {
        return relevance;
    }

    public void setRelevance(float relevance) {
        this.relevance = relevance;
    }
}
