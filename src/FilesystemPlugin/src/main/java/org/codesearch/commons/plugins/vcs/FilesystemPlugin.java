/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.vcs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * A VersionControlPlugin used to access files from the file system
 * @author David Froehlich
 */
@Component
public class FilesystemPlugin implements VersionControlPlugin {

    /** the folder where all code files are located */
    private String codeLocation;

    @Override
    public void setRepository(URI url, String username, String password) throws VersionControlPluginException {
        this.codeLocation = url.getPath();
    }

    @Override
    public String getFileContentForFilePath(String filePath) throws VersionControlPluginException {
        File f = new File(filePath);
        String content = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            while (br.ready()) {
                content += br.readLine();
            }
        } catch (IOException ex) {
            throw new VersionControlPluginException("File could not be opened: \n" + ex);
        }
        return content;
    }

    @Override
    public Set<String> getPathsForChangedFilesSinceRevision(String revision) throws VersionControlPluginException {
        Set<String> paths = new HashSet<String>();
        addChangedFilesFromDirectoryToSet(paths, new File(codeLocation), Long.parseLong(revision));
        return paths;
    }

    private void addChangedFilesFromDirectoryToSet(Set<String> paths, File directory, long lastModified) {
        for (File f : directory.listFiles()) {
            if (f.isDirectory()) {
                addChangedFilesFromDirectoryToSet(paths, f, lastModified);
            } else {
                if (f.lastModified() > lastModified) {
                    paths.add(f.getAbsolutePath());
                }
            }
        }
    }

    @Override
    public String getRepositoryRevision() throws VersionControlPluginException {
        return Long.toString(getHighestLastModifiedDateFromDirectory(new File(codeLocation)));
    }

    private long getHighestLastModifiedDateFromDirectory(File dir) {
        long lastModified = 0;
        for (File f : dir.listFiles()) {
            long lastMod;
            if (f.isDirectory()) {
                lastMod = getHighestLastModifiedDateFromDirectory(f);
            } else {
                lastMod = f.lastModified();
            }
            if (lastModified < lastMod) {
                lastModified = lastMod;
            }
        }
        return lastModified;
    }

    @Override
    public String getPurpose() {
        return "FILESYSTEM";
    }

    @Override
    public String getVersion() {
        return "0.1";
    }
}
