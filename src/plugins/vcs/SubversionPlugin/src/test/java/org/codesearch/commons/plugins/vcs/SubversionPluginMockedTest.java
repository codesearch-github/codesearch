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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.codesearch.commons.configuration.dto.RepositoryDto;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;

import static org.junit.Assert.assertEquals;

import static org.easymock.EasyMock.expect;

/**
 *
 * @author Marco.Michael
 *
 */
public class SubversionPluginMockedTest {

    private static final String SVN_URL = "/url/with";

    private static final String FILE_NAME = "file.txt";

    private static final String DIRECTORY_NAME = "directory";

    private SubversionPlugin plugin;

    private IMocksControl mockControl = EasyMock.createControl();

    private SVNRepository svnRepoMock;


    @Before
    public void setUp() throws SVNException {
        plugin = new SubversionPlugin() {

            @Override
            protected SVNRepository getSvnRepo() {
                return svnRepoMock;
            }

            @Override
            protected void createSvnRepo(RepositoryDto repository) throws SVNException, VersionControlPluginException {

            }
        };

    }

    private void initSVNRepoMocks(String url, String entrypoint) throws SVNException {
        svnRepoMock = mockControl.createMock(SVNRepository.class);

        SVNURL svnURL = mockControl.createMock(SVNURL.class);
        expect(svnURL.toDecodedString()).andReturn(url);
        expect(svnRepoMock.getRepositoryRoot(true)).andReturn(svnURL);

        SVNDirEntry entryDir = createSVNDirEntry(SVNNodeKind.DIR, DIRECTORY_NAME);
        expect(svnRepoMock.getDir(entrypoint, -1, null, (Collection)null)).andReturn(Arrays.asList(entryDir));

        SVNDirEntry entryFile = createSVNDirEntry(SVNNodeKind.FILE, FILE_NAME);
        expect(svnRepoMock.getDir(getEntryPointPrefix(entrypoint) + DIRECTORY_NAME, -1, null, (Collection)null))
            .andReturn(Arrays.asList(entryFile));
    }

    private String getEntryPointPrefix(String entrypoint) {
        if (StringUtils.isNotBlank(entrypoint)) {
            return entrypoint + "/";
        }
        return "";
    }

    private SVNDirEntry createSVNDirEntry(SVNNodeKind kind, String name) {
        SVNDirEntry entryDir = mockControl.createMock(SVNDirEntry.class);
        expect(entryDir.getName()).andReturn(name);
        expect(entryDir.getKind()).andReturn(kind).anyTimes();
        return entryDir;
    }

    @Test
    public void getFilesFromSubversionWithEntryPoint() throws Exception {
        RepositoryDto repositoryDto = createRepositoryDto("/entryPoint");
        initSVNRepoMocks(SVN_URL, "/entryPoint");
        mockControl.replay();
        plugin.setRepository(repositoryDto);
        Set<FileIdentifier> changedFilesSinceRevision = plugin.getChangedFilesSinceRevision(
            VersionControlPlugin.UNDEFINED_VERSION, new ArrayList<String>(), new ArrayList<String>());
        mockControl.verify();
        assertEquals(1, changedFilesSinceRevision.size());
        assertEquals(DIRECTORY_NAME + "/" + FILE_NAME, changedFilesSinceRevision.iterator().next().getFilePath());
    }

    private RepositoryDto createRepositoryDto(String entryPoint) {
        RepositoryDto repository = new RepositoryDto();
        repository.setUrl(SVN_URL + entryPoint);
        return repository;
    }

    @Test
    public void getFilesFromSubversionWithoutEntryPoint() throws Exception {

        RepositoryDto repositoryDto = createRepositoryDto("");
        initSVNRepoMocks(SVN_URL, "");
        mockControl.replay();

        plugin.setRepository(repositoryDto);
        Set<FileIdentifier> changedFilesSinceRevision = plugin.getChangedFilesSinceRevision(
            VersionControlPlugin.UNDEFINED_VERSION, new ArrayList<String>(), new ArrayList<String>());
        mockControl.verify();
        assertEquals(1, changedFilesSinceRevision.size());
        assertEquals(DIRECTORY_NAME + "/" + FILE_NAME, changedFilesSinceRevision.iterator().next().getFilePath());
    }
}
