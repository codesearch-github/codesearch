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

package org.codesearch.commons.configreader.xml.beans;

/**
 * Bean used to store information about a repository specified in the config.xml file
 * @author David Froehlich
 */
public class RepositoryBean {

    /** The name specified for the repository, stored in the <name> tag in the xml file */
    private String name;
    /** Specifies whether indexing should be enabled for this specific repository. Not recommended for
    repositories that will not be accessed or searched by codesearch*/
    private boolean indexingEnabled;
    /** Specifies whether the additional index will be created for the repository. */
    private boolean codeNavigationEnabled;

    /**
     * Creates a new instance of RepositoryBean setting all attributes to the given parameteres
     * @param name the name of the repository
     * @param indexingEnabled specifies whether the repository should have indexing enabled
     * @param codeNavigationEnabled specifies whether the repository should have the additional
     * indexes for the code navigation
     */
    public RepositoryBean(String name, boolean indexingEnabled, boolean codeNavigationEnabled) {
        this.name = name;
        this.indexingEnabled = indexingEnabled;
        this.codeNavigationEnabled = codeNavigationEnabled;
    }

    /**
     * Returns the value of codeNavigationEnabled
     * @return the value of CodeNavigationEnabled
     */
    public boolean isCodeNavigationEnabled() {
        return codeNavigationEnabled;
    }

    /**
     * Sets the value of codeNavigationEnabled to the given parameter
     * @param the value for codeNavigationEnabled
     */
    public void setCodeNavigationEnabled(boolean codeNavigationEnabled) {
        this.codeNavigationEnabled = codeNavigationEnabled;
    }

    /**
     * Returns the value of indexingEnabled
     * @return the value of indexingEnabled
     */
    public boolean isIndexingEnabled() {
        return indexingEnabled;
    }

    /**
     * sets the value of indexingEnabled to the given parameter
     * @param indexingEnabled the value for indexingEnabled
     */
    public void setIndexingEnabled(boolean indexingEnabled) {
        this.indexingEnabled = indexingEnabled;
    }

    /**
     * returns the name of the repository
     * @return the name of the repository
     */
    public String getName() {
        return name;
    }

    /**
     * sets the name of the repository to the given paramter
     * @param name the name for the repository
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        RepositoryBean other = (RepositoryBean) o;
        if(this.getName().equals(other.getName()) && this.isCodeNavigationEnabled() == other.isCodeNavigationEnabled() && this.isIndexingEnabled() == other.isIndexingEnabled()){
            return true;
        }
        return false;
    }
}
