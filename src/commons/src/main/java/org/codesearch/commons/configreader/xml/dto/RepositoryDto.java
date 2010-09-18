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
package org.codesearch.commons.configreader.xml.dto;

/**
 * DTO used to store information about a repository specified in the config.xml file
 * @author David Froehlich
 */
public class RepositoryDto {

    /** The name specified for the repository, stored in the <name> tag in the xml file */
    private String name;
    /** The URL of the repository. */
    private String url;
    /** The username for this repository. */
    private String username;
    /** The password for this repository. */
    private String password;
    /** Specifies whether the additional index will be created for the repository. */
    private boolean codeNavigationEnabled;
    /** The version control system used for this repository, (for instance SVN or Bazaar),
     * must match the purpose attribute of the corresponding version control plugin */
    private String versionControlSystem;

    public RepositoryDto() {
    }

    /**
     * Creates a new instance setting all attributes to the given parameters.
     * @param name the name of the repository
     * @param indexingEnabled specifies whether the repository should have indexing enabled
     * @param codeNavigationEnabled specifies whether the repository should have the additional
     * indexes for the code navigation
     */
    public RepositoryDto(String name, String url, String username, String password, boolean codeNavigationEnabled, String versionControlSystem) {
        this.name = name;
        this.url = url;
        this.username = username;
        this.password = password;
        this.codeNavigationEnabled = codeNavigationEnabled;
        this.versionControlSystem = versionControlSystem;
    }

    public boolean isCodeNavigationEnabled() {
        return codeNavigationEnabled;
    }

    public void setCodeNavigationEnabled(boolean codeNavigationEnabled) {
        this.codeNavigationEnabled = codeNavigationEnabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVersionControlSystem() {
        return versionControlSystem;
    }

    public void setVersionControlSystem(String versionControlSystem) {
        this.versionControlSystem = versionControlSystem;
    }

    /**
     * compares this RepositoryDto with the given one
     * @param o the RepositoryDto to compare
     * @return true if all the attributes are equal
     */
    @Override
    public boolean equals(Object o) {
        try {
            RepositoryDto other = (RepositoryDto) o;
            if (this.getName().equals(other.getName()) && this.isCodeNavigationEnabled() == other.isCodeNavigationEnabled()) {
                return true;
            }
            return false;
        } catch(ClassCastException ex){
            return false;
        }
    }
}
