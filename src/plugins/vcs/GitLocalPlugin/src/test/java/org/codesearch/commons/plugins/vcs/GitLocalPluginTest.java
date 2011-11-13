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
/*
 *
 *
 */
package org.codesearch.commons.plugins.vcs;

import org.codesearch.commons.configuration.dto.NoAuthentication;
import org.codesearch.commons.configuration.dto.RepositoryDto;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests {@link GitLocalPlugin}.
 * @author Samuel Kogler
 */
public class GitLocalPluginTest {

    private VersionControlPlugin plugin;

    public GitLocalPluginTest() {
        plugin = new GitLocalPlugin();
    }

    private RepositoryDto getTestRepo() {
        RepositoryDto testRepo = new RepositoryDto();
        testRepo.setName("log4j");
        testRepo.setUrl("git://github.com/apache/log4j.git");
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
        System.out.println("getFileDtoForFileIdentifier");
        FileIdentifier fileIdentifier = new FileIdentifier();
        fileIdentifier.setFilePath("LICENSE");
        fileIdentifier.setRepository(getTestRepo());

        FileDto result = plugin.getFileDtoForFileIdentifier(fileIdentifier);

        System.out.println("File path: " + result.getFilePath());
        System.out.println("Binary: " + result.isBinary());
        System.out.println("File content: ");
        System.out.println(new String(result.getContent()));
    }

    /**
     * Test of getChangedFilesSinceRevision method, of class GitLocalPlugin.
     */
    @Test
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

    /**
     * Test of getFilesInDirectory method, of class GitLocalPlugin.
     */
    @Test
    public void testGetFilesInDirectory() throws Exception {
        System.out.println("getFilesInDirectory");
        String directoryPath = "src";
        List<String> files = plugin.getFilesInDirectory(directoryPath);
        System.out.println("Files in directory src/: \n" + files);
    }
}
