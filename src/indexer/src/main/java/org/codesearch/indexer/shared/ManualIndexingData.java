/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.indexer.shared;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author david
 */
public class ManualIndexingData implements Serializable {

    private List<String> repositories;
    private List<String> repositoryGroups;

    public ManualIndexingData(List<String> repositories, List<String> repositoryGroups) {
        this.repositories = repositories;
        this.repositoryGroups = repositoryGroups;
    }

    public List<String> getRepositories() {
        return repositories;
    }

    public List<String> getRepositoryGroups() {
        return repositoryGroups;
    }

    public void setRepositories(List<String> repositories) {
        this.repositories = repositories;
    }

    public void setRepositoryGroups(List<String> repositoryGroups) {
        this.repositoryGroups = repositoryGroups;
    }

    public ManualIndexingData() {
    }
}
