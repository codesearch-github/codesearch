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
package org.codesearch.commons.plugins.vcs;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

/**
 * A plugin used to access files stored in Subversion repositories.
 * @author David Froehlich
 * @author Samuel Kogler
 */
@Component
public class SubversionPlugin implements VersionControlPlugin {

    /** The repository that is currently accessed. */
    private SVNRepository repository;

    public SubversionPlugin() {
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        FSRepositoryFactory.setup();
    }

    /** {@inheritDoc} */
    @Override
    public String getPurpose() {
        return "SVN";
    }

    /** {@inheritDoc} */
    @Override
    public String getVersion() {
        return "0.1";
    }

    /** {@inheritDoc} */
    @Override
    public void setRepository(URL url, String username, String password) throws VersionControlPluginException {
        try {
            SVNURL svnurl = SVNURL.parseURIDecoded(url.toString());
            repository = SVNRepositoryFactory.create(svnurl);

            ISVNAuthenticationManager authManager = new BasicAuthenticationManager(username, password);
            repository.setAuthenticationManager(authManager);
        } catch (SVNException ex) {
            throw new VersionControlPluginException(ex.toString());
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getFileContentForFilePath(String filePath) throws VersionControlPluginException {
        try {
            SVNNodeKind nodeKind = repository.checkPath(filePath, -1);
            if (nodeKind != SVNNodeKind.FILE) {
                return "";
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            repository.getFile(filePath, -1, null, baos);
            return baos.toString();
        } catch (SVNException ex) {
            throw new VersionControlPluginException(ex.toString());
        }
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> getPathsForChangedFilesSinceRevision(String revision) throws VersionControlPluginException {
        Set<String> paths = new HashSet();

        Collection logs;
        try {
            logs = repository.log(new String[]{}, null, Integer.parseInt(revision), -1, true, false);
            Iterator iter = logs.iterator();
            while (iter.hasNext()) {
                SVNLogEntry entry = (SVNLogEntry) iter.next();
                Iterator iter2 = entry.getChangedPaths().keySet().iterator();
                while (iter2.hasNext()) {
                    paths.add((String) iter2.next());
                }
            }
        } catch (SVNException ex) {
            throw new VersionControlPluginException(ex.toString());
        }
        return paths;
    }
}
