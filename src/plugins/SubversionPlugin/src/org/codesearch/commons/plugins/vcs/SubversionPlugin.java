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

import java.util.Set;

/**
 *
 * @author daasdingo
 */
public class SubversionPlugin implements VersionControlPlugin {

    public void setRepositoryURL(String url) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getFileContentForFilePath(String filePath) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<String> getPathsForChangedFilesSinceRevision(String revision) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getAssociatedRepositoryType() {
        return "SVN";
    }

}
