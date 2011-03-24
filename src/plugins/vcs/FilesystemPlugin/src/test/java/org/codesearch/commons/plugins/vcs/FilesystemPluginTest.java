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
import org.apache.log4j.Logger;

import org.junit.Test;

/**
 *
 * @author David Froehlich
 */
public class FilesystemPluginTest {

     /* Logger */
    private static final Logger LOG = Logger.getLogger(FilesystemPluginTest.class);
    /**
     * Test of getFileContentForFilePath method, of class FilesystemPlugin.
     */
    @Test
    public void testGetFileContentForFilePath() throws Exception {
        FilesystemPlugin fsPlugin = new FilesystemPlugin();
        Set<FileDto> files = fsPlugin.getChangedFilesSinceRevision("0");
        FileDto file = (FileDto) files.toArray()[0];
        byte[] content =  fsPlugin.getFileForFilePath(file.getFilePath()).getContent();
        LOG.info(new String(content));
    }
}
