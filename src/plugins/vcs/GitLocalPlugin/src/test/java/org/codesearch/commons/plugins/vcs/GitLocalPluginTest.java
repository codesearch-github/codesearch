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
/*
 *
 *
 */
package org.codesearch.commons.plugins.vcs;

import java.util.LinkedList;
import java.util.Set;
import org.codesearch.commons.configuration.dto.NoAuthentication;
import org.codesearch.commons.configuration.dto.RepositoryDto;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests {@link GitLocalPlugin}.
 *
 * @author Samuel Kogler
 */
public class GitLocalPluginTest {

    private VersionControlPlugin plugin;

    public GitLocalPluginTest() throws VersionControlPluginException {
        plugin = new GitLocalPlugin();
        plugin.setCacheDirectory("/tmp"); // specify the cache directory used during the tests
    }

    private RepositoryDto getTestRepo() {
        RepositoryDto testRepo = new RepositoryDto();
        testRepo.setName("codesearch-test-repo");
        testRepo.setUrl("git://github.com/codesearch-github/codesearch-test-repo.git");
        testRepo.setVersionControlSystem("GIT");
        testRepo.setCodeNavigationEnabled(false);
        testRepo.setUsedAuthentication(new NoAuthentication());
        testRepo.setWhitelistEntries(new LinkedList<String>());
        testRepo.setBlacklistEntries(new LinkedList<String>());
        testRepo.setRepositoryGroups(new LinkedList<String>());
        return testRepo;
    }

    /**
     * Test of setRepository method, of class GitLocalPlugin.
     */
    @Test
    @Before
    public void testSetRepository() throws Exception {
        //Checking out code
        plugin.setRepository(getTestRepo());
    }

    /**
     * Test of getFileDtoForFileIdentifier method, of class GitLocalPlugin.
     */
    @Test
    public void testGetFileDtoForFileIdentifier() throws Exception {
        FileIdentifier fileIdentifier = new FileIdentifier();
        fileIdentifier.setFilePath("test.java");
        fileIdentifier.setRepository(getTestRepo());

        FileDto result = plugin.getFileDtoForFileIdentifierAtRevision(fileIdentifier, VersionControlPlugin.UNDEFINED_VERSION);
        assert ("version 3\n".equals(new String(result.getContent())));
    }

    private void testGetOldFile() throws Exception {
        FileIdentifier fileIdentifier = new FileIdentifier();
        fileIdentifier.setFilePath("test.java");
        fileIdentifier.setRepository(getTestRepo());

        FileDto result = plugin.getFileDtoForFileIdentifierAtRevision(fileIdentifier, "a471655110aba4ce116335fba6e0de5fa4cc5870"); //version 2
        assert ("version 2".equals(new String(result.getContent())));
    }

    /**
     * Test of getChangedFilesSinceRevision method, of class GitLocalPlugin.
     */
    @Test
    @Ignore
    public void testGetChangedFilesSinceRevision() throws Exception {
        System.out.println("getChangedFilesSinceRevision");
        String revision = "0";
        Set<FileIdentifier> changes = plugin.getChangedFilesSinceRevision(revision);
        System.out.println("changes returned");
        assertNotNull(changes);
        assert (!changes.isEmpty());
        System.out.println("Changed files since revision 0: ");
        for (FileIdentifier fileDto : changes) {
            System.out.println(fileDto.getFilePath());
        }
    }

    /**
     * Test of getRepositoryRevision method, of class GitLocalPlugin.
     */
    @Test
    public void testGetRepositoryRevision() throws Exception {
        System.out.println("getRepositoryRevision");
        String result = plugin.getRepositoryRevision();
        System.out.println("Current revision: " + result);
        assertNotNull(result);
        assert (!"0".equals(result));
    }

}
