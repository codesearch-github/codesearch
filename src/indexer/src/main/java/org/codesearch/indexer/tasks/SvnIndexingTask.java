/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
