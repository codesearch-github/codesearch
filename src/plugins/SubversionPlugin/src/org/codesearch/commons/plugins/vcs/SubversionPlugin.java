
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
