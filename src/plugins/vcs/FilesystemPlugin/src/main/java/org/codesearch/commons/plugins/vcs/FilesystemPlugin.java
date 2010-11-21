package org.codesearch.commons.plugins.vcs;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.codesearch.commons.constants.MimeTypeNames;
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
    private int i = 0;

    /** {@inheritDoc} */
    @Override
    public void setRepository(URI url, String username, String password) throws VersionControlPluginException {
        this.codeLocation = url.getPath();
    }

    /** {@inheritDoc} */
    @Override
    public ByteArrayOutputStream getFileContentForFilePath(String filePath) throws VersionControlPluginException {
        File file = new File(filePath);
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte fileContent[] = new byte[(int) file.length()];
            fis.read(fileContent);
            baos.write(fileContent);
            return baos;
        } catch (IOException ex) {
            throw new VersionControlPluginException("File could not be opened: \n" + ex);
        }
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
        Date d = new Date();
        for (File f : directory.listFiles()) {
            if (f.isDirectory()) {
                addChangedFilesFromDirectoryToSet(files, f, lastModified);
            } else {
                if (f.lastModified() > lastModified) {
                    ByteArrayOutputStream baos = getFileContentForFilePath(f.getAbsolutePath());
                    //TODO replace work-around as soon as stephans mime type analyzer works
                    String mimeType = "";
                    if (f.getAbsolutePath().endsWith(".java")) {
                        mimeType = MimeTypeNames.JAVA;
                    }
                    mimeType = CommonsUtils.getMimeTypeForFile(baos);
                    files.add(new FileDto(f.getAbsolutePath(), baos, mimeType));
                }
            }
        }
        System.out.println("Mime time: " + (new Date().getTime() - d.getTime()));
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
