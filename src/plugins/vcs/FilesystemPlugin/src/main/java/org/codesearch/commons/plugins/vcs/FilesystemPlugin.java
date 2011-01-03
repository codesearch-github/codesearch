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
import java.io.File;
import java.io.FileInputStream;
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

    /** {@inheritDoc} */
    @Override
    public void setRepository(URI url, String username, String password) throws VersionControlPluginException {
        this.codeLocation = url.getPath();
    }

    /** {@inheritDoc} */
    @Override
    public FileDto getFileForFilePath(String filePath) throws VersionControlPluginException {
        
        try {
            File file = new File(filePath);
            FileDto fileDto = new FileDto();

            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte fileContent[] = new byte[(int) file.length()];
            fis.read(fileContent);

            //TODO find method to guess this quickly
            // assume that files are not binary because MIME detection takes too long

            fileDto.setFilePath(filePath);
            fileDto.setContent(fileContent);
            fileDto.setBinary(false);

            return fileDto;
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
     * @param files the set
     * @param directory the current directory
     * @param lastModified the time of last indexing
     */
    private void addChangedFilesFromDirectoryToSet(Set<FileDto> files, File directory, long lastModified) throws VersionControlPluginException {
        for (File f : directory.listFiles()) {
            if (f.isDirectory()) {
                addChangedFilesFromDirectoryToSet(files, f, lastModified);
            } else {
                if (f.lastModified() > lastModified) {
                    files.add(getFileForFilePath(f.getAbsolutePath()));
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