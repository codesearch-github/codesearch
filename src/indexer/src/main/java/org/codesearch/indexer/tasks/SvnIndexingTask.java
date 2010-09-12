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

package org.codesearch.indexer.tasks;

import java.io.File;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.codesearch.commons.constants.IndexConstants;
import org.codesearch.commons.plugins.PluginLoader;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;

/**
 * This class is used to index a svn repository.
 * 
 * @author Stephan Stiboller
 */
public class SvnIndexingTask extends IndexingTask {

    @Override
    public void execute() {
        try {
            initializeVersionControlPlugin();
            initializeIndexWriter(new StandardAnalyzer(IndexConstants.LUCENE_VERSION), new File(IndexConstants.INDEX_DIRECTORY));
            createIndex();
        } catch (Exception ex) {
            log.error("Task execution failed: " + ex.getMessage());
        }
    }

    @Override
    public void initializeVersionControlPlugin()
    {
        try {
            PluginLoader pl = new PluginLoader(VersionControlPlugin.class);
            vcp = (VersionControlPlugin) pl.getPluginForPurpose("SVN");
            filesNames = vcp.getPathsForChangedFilesSinceRevision("0"); // TODO : REVISION EXTRACTING!
        } catch (Exception ex) {
            log.error("Task execution failed: " + ex.getMessage());
        }
    }

}
