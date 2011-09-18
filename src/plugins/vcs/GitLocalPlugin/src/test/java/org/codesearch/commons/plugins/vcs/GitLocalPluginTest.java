/*
 *
 *
 */
package org.codesearch.commons.plugins.vcs;

import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
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
     * Test of getFileForFilePath method, of class GitLocalPlugin.
     */
    @Test
    public void testGetFileForFilePath() throws Exception {
        System.out.println("getFileForFilePath");
        String filePath = "LICENSE";
        FileIdentifier result = plugin.getFileForFilePath(filePath);

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
