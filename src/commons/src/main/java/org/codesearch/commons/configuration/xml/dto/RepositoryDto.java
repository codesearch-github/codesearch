/**
 * Copyright 2010 David Froehlich <david.froehlich@businesssoftware.at>, Samuel Kogler <samuel.kogler@gmail.com>, Stephan Stiboller
 * <stistc06@htlkaindorf.at>
 * 
 * This file is part of Codesearch.
 * 
 * Codesearch is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * Codesearch is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Codesearch. If not, see <http://www.gnu.org/licenses/>.
 */
package org.codesearch.commons.configuration.xml.dto;

import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.codesearch.commons.plugins.vcs.AuthenticationType;

/**
 * DTO used to store information about a repository specified in the config.xml file
 * 
 * @author David Froehlich
 */
public class RepositoryDto {

    /** The name specified for the repository, stored in the <name> tag in the xml file */
    private String name;
    /** The URL of the repository. */
    private String url;
    /** Specifies whether the additional index will be created for the repository. */
    private boolean codeNavigationEnabled;
    /**
     * The version control system used for this repository, (for instance SVN or Bazaar), must match the purpose attribute of the
     * corresponding version control plugin
     */
    private String versionControlSystem;
    /** A list of file name patterns that the files that will be indexed have to match (every file has to match at least one of the entries */
    private List<String> whitelistEntries;
    /** A list of all file names (in regex) that will not be indexed in this repository */
    private List<String> blacklistEntries;
    /** A list of repository groups this repositorie belongs to */
    private List<String> repositoryGroups;
    /** The authentication type used to access the files in this repository */
    private AuthenticationType usedAuthentication;

    public RepositoryDto() {
    }

    /**
     * Creates a new instance of RepositoryDto
     * 
     * @param name the unique name of the repository
     * @param url the url used by the VersionControlPlugin to access the repository
     * @param usedAuthentication the authentication type used by the plugin
     * @param codeNavigationEnabled determines whether code navigation is enabled for this repository
     * @param versionControlSystem determines which VersionControlSystem is used for the repository, must match the string returned by the
     *            getPurpose method of the VersionControlPlugin
     * @param blacklistEntries the list of regex strings representing the filenames of files that will not be indexed
     * @param whitelistEntries the list of regex strings a filename must match in order for the file to be indexed (only one entry has to be
     *            matched)
     * @param repositoryGroups the groups this repository belongs to
     */
    public RepositoryDto(String name, String url, AuthenticationType usedAuthentication, boolean codeNavigationEnabled,
            String versionControlSystem, List<String> blacklistEntries, List<String> whitelistEntries, List<String> repositoryGroups) {
        this.name = name;
        this.url = url;
        this.usedAuthentication = usedAuthentication;
        this.codeNavigationEnabled = codeNavigationEnabled;
        this.versionControlSystem = versionControlSystem;
        this.blacklistEntries = blacklistEntries;
        this.repositoryGroups = repositoryGroups;
        this.whitelistEntries = whitelistEntries;
    }

    public List<String> getWhitelistEntries() {
        return whitelistEntries;
    }

    public void setWhitelistEntries(List<String> whitelistEntries) {
        this.whitelistEntries = whitelistEntries;
    }

    public List<String> getBlacklistEntries() {
        return blacklistEntries;
    }

    public void setBlacklistEntries(List<String> blacklistEntries) {
        this.blacklistEntries = blacklistEntries;
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

    public String getVersionControlSystem() {
        return versionControlSystem;
    }

    public void setVersionControlSystem(String versionControlSystem) {
        this.versionControlSystem = versionControlSystem;
    }

    public void setUsedAuthentication(AuthenticationType usedAuthentication) {
        this.usedAuthentication = usedAuthentication;
    }

    public AuthenticationType getUsedAuthentication() {
        return usedAuthentication;
    }

    /**
     * @return the repositoryGroups
     */
    public List<String> getRepositoryGroups() {
        return repositoryGroups;
    }

    /**
     * Return the repository list as a single string the groups are spearated with a whitespace
     * 
     * @return repository list as a single string separated by whitspaces
     */
    public String getRepositoryGroupsAsString() {
        String repoString = "";
        for (String repo : repositoryGroups) {
            repoString += repo + " ";
        }
        return repoString;
    }

    /**
     * @param repositoryGroups the repositoryGroups to set
     */
    public void setRepositoryGroups(List<String> repositoryGroups) {
        this.repositoryGroups = repositoryGroups;
    }

    /**
     * compares this RepositoryDto with the given one
     * 
     * @param o the RepositoryDto to compare
     * @return true if all the attributes are equal
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof RepositoryDto)) {
            return false;
        }
        RepositoryDto other = (RepositoryDto) o;
        if(name == null || other.getName() == null){
            return false;
        }
        if (name.equals(other.getName())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        String retString = "";
        retString += "Name: " + name + ", url: " + url + ", used authentication: " + usedAuthentication.toString();
        return retString;
    }
}
