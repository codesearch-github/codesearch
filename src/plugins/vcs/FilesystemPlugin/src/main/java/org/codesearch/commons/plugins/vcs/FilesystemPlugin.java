package org.codesearch.commons.plugins.vcs;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codesearch.commons.utils.CommonsUtils;
import org.springframework.stereotype.Component;

/**
 * A VersionControlPlugin used to access files from the file system
 * @author David Froehlich
 */
@Component
public class FilesystemPlugin implements VersionControlPlugin {

    /** the folder where all code files are located */
    private String codeLocation;

    /** {@inheritDoc} */
    @Override
    public void setRepository(URI url, String username, String password) throws VersionControlPluginException {
        this.codeLocation = url.getPath();
    }

    /** {@inheritDoc} */
    @Override
    public ByteArrayOutputStream getFileContentForFilePath(String filePath) throws VersionControlPluginException {
        File f = new File(filePath);
        ByteArrayOutputStream content = new ByteArrayOutputStream();
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            while (br.ready()) {
                content.write(br.readLine().getBytes());
            }
        } catch (IOException ex) {
            throw new VersionControlPluginException("File could not be opened: \n" + ex);
        }
        return content;
    }

    /** {@inheritDoc} */
    @Override
    public Set<FileDto> getChangedFilesSinceRevision(String revision) throws VersionControlPluginException {
        Set<FileDto> files = new HashSet<FileDto>();
        addChangedFilesFromDirectoryToSet(files, new File(codeLocation), Long.parseLong(revision));
        return files;
    }

    /**
     * recursively adds all changed files from the directory into the set
     * @param paths the set
     * @param directory the current directory
     * @param lastModified the time of last indexing
     */
    private void addChangedFilesFromDirectoryToSet(Set<FileDto> files, File directory, long lastModified) throws VersionControlPluginException {

        for (File f : directory.listFiles()) {
            if (f.isDirectory()) {
                addChangedFilesFromDirectoryToSet(files, f, lastModified);
            } else {
                if (f.lastModified() > lastModified) {
                    ByteArrayOutputStream baos = getFileContentForFilePath(f.getAbsolutePath());
                    files.add(new FileDto(f.getAbsolutePath(), baos, CommonsUtils.getMimeTypeForFile(baos)));
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getRepositoryRevision() throws VersionControlPluginException {
        return Long.toString(getHighestLastModifiedDateFromDirectory(new File(codeLocation)));
    }

    /**
     * recursively finds the highest last modified date from the directory
     * @param dir the current directory
     * @return the highest modification date in the directory
     */
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

    /** {@inheritDoc} */
    @Override
    public String getPurposes() {
        return "FILESYSTEM";
    }

    /** {@inheritDoc} */
    @Override
    public String getVersion() {
        return "0.1";
    }
}
