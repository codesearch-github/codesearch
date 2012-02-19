/**
 * Copyright 2010 David Froehlich <david.froehlich@businesssoftware.at>, Samuel
 * Kogler <samuel.kogler@gmail.com>, Stephan Stiboller <stistc06@htlkaindorf.at>
 *
 * This file is part of Codesearch.
 *
 * Codesearch is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Codesearch is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Codesearch. If not, see <http://www.gnu.org/licenses/>.
 */
package org.codesearch.commons.plugins.vcs;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.codesearch.commons.configuration.dto.NoAuthentication;
import org.codesearch.commons.configuration.dto.RepositoryDto;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author David Froehlich
 */
public class SubversionPluginTest {

    SubversionPlugin sp = new SubversionPlugin();

    public SubversionPluginTest() {
    }

    @Before
    public void setUp() throws VersionControlPluginException {
        RepositoryDto repositoryDto = new RepositoryDto();
        repositoryDto.setName("codesearch-test-repo");
        repositoryDto.setUrl("svn://svn.code.sf.net/p/codesearchtest/code/trunk");
        repositoryDto.setVersionControlSystem("SVN");
        repositoryDto.setUsedAuthentication(new NoAuthentication());
        sp.setRepository(repositoryDto);
    }

    @Test
    public void testGetFile() throws VersionControlPluginException {
        FileDto file = sp.getFileDtoForFileIdentifierAtRevision(new FileIdentifier("testfile", false, null), VersionControlPlugin.UNDEFINED_VERSION);
        assert "version 3\n".equals(new String(file.getContent()));
    }

    @Test
    public void testGetOldFile() throws VersionControlPluginException {
        FileDto file = sp.getFileDtoForFileIdentifierAtRevision(new FileIdentifier("testfile", false, null), "2");
        assert "version 1\n".equals(new String(file.getContent()));
    }

    /**
     * Tests whether the paths that are returned by
     * getChangedFilesSinceRevision are valid.
     * Does not define a start revision.
     */
    @Test
    public void testGetChangedFileWithBlacklistEntries() throws VersionControlPluginException {
        List<String> blacklistPatterns = new LinkedList<String>();
        blacklistPatterns.add(".*trunk.*");
        
        Set<FileIdentifier> changedFilesSinceRevision = sp.getChangedFilesSinceRevision(VersionControlPlugin.UNDEFINED_VERSION, blacklistPatterns, Collections.<String>emptyList());
        assert (changedFilesSinceRevision.isEmpty());
    }
    
    /**
     * Tests whether the paths that are returned by
     * getChangedFilesSinceRevision are valid.
     * Does not define a start revision.
     */
    @Test
    public void testGetFilesFromStart() throws VersionControlPluginException {
        Set<FileIdentifier> changedFilesSinceRevision = sp.getChangedFilesSinceRevision(VersionControlPlugin.UNDEFINED_VERSION, Collections.<String>emptyList(), Collections.<String>emptyList());
        assert (!changedFilesSinceRevision.isEmpty());
        for (FileIdentifier fileIdentifier : changedFilesSinceRevision) {
            if (!fileIdentifier.isDeleted()) {
                FileDto fileDto = sp.getFileDtoForFileIdentifierAtRevision(fileIdentifier, VersionControlPlugin.UNDEFINED_VERSION);
                assert (fileDto != null);
            }
        }
    }

//    /**
//     * Tests whether the paths that are returned by
//     * getChangedFilesSinceRevision are valid.
//     * Tests using a start revision.
//     */
//    @Test
//    public void testGetFilesFromRev() throws VersionControlPluginException {
//        Set<FileIdentifier> changedFilesSinceRevision = sp.getChangedFilesSinceRevision("2");
//        assert (!changedFilesSinceRevision.isEmpty());
//        for (FileIdentifier fileIdentifier : changedFilesSinceRevision) {
//            if (!fileIdentifier.isDeleted()) {
//                FileDto fileDto = sp.getFileDtoForFileIdentifierAtRevision(fileIdentifier, VersionControlPlugin.UNDEFINED_VERSION);
//                assert (fileDto != null);
//            }
//        }
//    }

    @Test
    public void testListDir() throws VersionControlPluginException {
        List<String> filesInDirectory = sp.getFilesInDirectory("/", VersionControlPlugin.UNDEFINED_VERSION);
        assert (filesInDirectory.size() == 2);
    }
}
