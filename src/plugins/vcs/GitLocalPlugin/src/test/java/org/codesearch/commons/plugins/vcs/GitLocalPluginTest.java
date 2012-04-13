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

import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.codesearch.commons.configuration.dto.NoAuthentication;
import org.codesearch.commons.configuration.dto.RepositoryDto;
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
        plugin.pullChanges();
    }

    /**
     * Test of getFileDtoForFileIdentifier method, of class GitLocalPlugin.
     */
    @Test
    public void testGetFileDtoForFileIdentifier() throws Exception {
        FileIdentifier fileIdentifier = new FileIdentifier();
        fileIdentifier.setFilePath("test.java");
        fileIdentifier.setRepository(getTestRepo());

        FileDto result = plugin.getFile(fileIdentifier, VersionControlPlugin.UNDEFINED_VERSION);
        assert (result != null);
        assert ("version 3\n".equals(new String(result.getContent())));
        assert (StringUtils.isNotBlank(result.getFilePath()));
        assert (StringUtils.isNotBlank(result.getLastAlteration()));
        assert (StringUtils.isNotBlank(result.getLastAuthor()));
        assert (result.getLastAlteration().indexOf('\n') == -1);
        assert (result.getLastAuthor().indexOf('\n') == -1);
    }

    @Test
    public void testGetOldFile() throws Exception {
        FileIdentifier fileIdentifier = new FileIdentifier();
        fileIdentifier.setFilePath("test.java");
        fileIdentifier.setRepository(getTestRepo());

        FileDto result = plugin.getFile(fileIdentifier, "a471655110aba4ce116335fba6e0de5fa4cc5870"); //version 2
        assert ("version 2\n".equals(new String(result.getContent())));
    }

    @Test
    public void testGetInvalidFile() throws Exception {
        FileIdentifier fileIdentifier = new FileIdentifier();
        fileIdentifier.setFilePath("testt.java");
        fileIdentifier.setRepository(getTestRepo());
        boolean notFound = false;
        try {
            FileDto result = plugin.getFile(fileIdentifier, "a471655110aba4ce116335fba6e0de5fa4cc5870");
        } catch (VcsFileNotFoundException ex) {
            notFound = true;
        }
        assert notFound;
    }

    /**
     * Test of getChangedFilesSinceRevision method, of class GitLocalPlugin.
     */
    @Test
    public void testGetChangedFilesSinceRevision() throws Exception {
        String revision = VersionControlPlugin.UNDEFINED_VERSION;
        Set<FileIdentifier> changes = plugin.getChangedFilesSinceRevision(revision, Collections.<String>emptyList(), Collections.<String>emptyList());
        assert (changes != null);
        assert (changes.size() == 3);
    }

    /**
     * Test of getRepositoryRevision method, of class GitLocalPlugin.
     */
    @Test
    public void testGetRepositoryRevision() throws Exception {
        String result = plugin.getRepositoryRevision();
        assert (result != null);
        assert (!"0".equals(result));
    }
}
