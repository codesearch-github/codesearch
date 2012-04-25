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

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;

import static org.junit.Assert.assertEquals;

import static org.easymock.EasyMock.expect;

/**
 *
 * @author Marco.Michael
 *
 */
public class SubversionPluginMockedTest {

    private static final String FILE_NAME = "file.txt";

    private static final String DIRECTORY_NAME = "directory";

    private SubversionPlugin plugin;

    private IMocksControl mockControl = EasyMock.createControl();

    protected SVNRepository repo;

    @Before
    public void setUp() throws SVNException {
        initSVNRepoMocks();

        plugin = new SubversionPlugin() {

            @Override
            protected SVNRepository getSvnRepo() {
                return repo;
            }
        };

    }

    private void initSVNRepoMocks() throws SVNException {
        repo = mockControl.createMock(SVNRepository.class);

        SVNDirEntry entryDir = createSVNDirEntry(SVNNodeKind.DIR, DIRECTORY_NAME);
        expect(repo.getDir("", -1, null, (Collection)null)).andReturn(Arrays.asList(entryDir));

        SVNDirEntry entryFile = createSVNDirEntry(SVNNodeKind.FILE, FILE_NAME);
        expect(repo.getDir(DIRECTORY_NAME, -1, null, (Collection)null)).andReturn(Arrays.asList(entryFile));

    }

    private SVNDirEntry createSVNDirEntry(SVNNodeKind kind, String name) {
        SVNDirEntry entryDir = mockControl.createMock(SVNDirEntry.class);
        expect(entryDir.getName()).andReturn(name);
        expect(entryDir.getKind()).andReturn(kind).anyTimes();
        return entryDir;
    }

    @Test
    public void getFilesFromSubversionWithEntryPoint() throws Exception {
        mockControl.replay();
        Set<FileIdentifier> changedFilesSinceRevision = plugin.getChangedFilesSinceRevision(
            VersionControlPlugin.UNDEFINED_VERSION, new ArrayList<String>(), new ArrayList<String>());
        mockControl.verify();
        assertEquals(1, changedFilesSinceRevision.size());
        assertEquals(DIRECTORY_NAME + "/" + FILE_NAME, changedFilesSinceRevision.iterator().next().getFilePath());
    }

    @Test
    public void getFilesFromSubversionWithoutEntryPoint() throws Exception {
        mockControl.replay();
        Set<FileIdentifier> changedFilesSinceRevision = plugin.getChangedFilesSinceRevision(VersionControlPlugin.UNDEFINED_VERSION, new ArrayList<String>(),
            new ArrayList<String>());
        mockControl.verify();
        assertEquals(1, changedFilesSinceRevision.size());
        assertEquals(DIRECTORY_NAME + "/" + FILE_NAME, changedFilesSinceRevision.iterator().next().getFilePath());
    }
}
