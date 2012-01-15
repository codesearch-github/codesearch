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

import org.codesearch.commons.configuration.dto.RepositoryDto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.codesearch.commons.validator.ValidationException;

/**
 * A VersionControlPlugin used to access files from the file system
 * @author David Froehlich
 */
public class FilesystemPlugin implements VersionControlPlugin {

    /** the folder where all code files are located */
    private RepositoryDto repository;

    /** {@inheritDoc} */
    @Override
    public void setRepository(RepositoryDto repository) throws VersionControlPluginException {
        this.repository = repository;
    }

    /** {@inheritDoc} */
    @Override
    public FileDto getFileDtoForFileIdentifierAtRevision(FileIdentifier identifier, String revision) throws VersionControlPluginException {
        try {
            File file = new File(identifier.getFilePath());
            FileInputStream fis = new FileInputStream(file);
            byte fileContent[] = new byte[(int) file.length()];
            fis.read(fileContent);
            fis.close();
            FileDto fileDto = new FileDto(identifier.getFilePath(), "unknown", new Date(file.lastModified()).toString(), fileContent, repository, false);
            return fileDto;
        } catch (IOException ex) {
            throw new VersionControlPluginException("File could not be opened: \n" + ex);
        } catch (NullPointerException ex) {
            throw new VersionControlPluginException("File not found: \n" + ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Set<FileIdentifier> getChangedFilesSinceRevision(String revision) throws VersionControlPluginException {
        Set<FileIdentifier> files = new HashSet();
        addChangedFilesFromDirectoryToSet(files, new File(repository.getUrl()), Long.parseLong(revision));
        return files;
    }

    /**
     * recursively adds all changed files from the directory into the set
     * @param files the set
     * @param directory the current directory
     * @param lastModified the time of last indexing
     */
    private void addChangedFilesFromDirectoryToSet(Set<FileIdentifier> files, File directory, long lastModified) throws VersionControlPluginException {
        for (File currentFile : directory.listFiles()) {
            if (currentFile.isDirectory()) {
                addChangedFilesFromDirectoryToSet(files, currentFile, lastModified);
            } else if (currentFile.lastModified() > lastModified) {
                files.add(new FileIdentifier(currentFile.getAbsolutePath(), false, false, repository));
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getRepositoryRevision() throws VersionControlPluginException {
        try {
        return Long.toString(getHighestLastModifiedDateFromDirectory(new File(repository.getUrl())));
        }catch (NullPointerException ex){
            throw new VersionControlPluginException("The directory specified as a local repository could not be opened: "+ repository.getUrl());
        }
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

    @Override
    public List<String> getFilesInDirectory(String directoryPath, String revision) throws VersionControlPluginException {
        //intentionally ignore the revision parameter
        File f = new File(directoryPath);
        return Arrays.asList(f.list());
    }

    @Override
    public void setCacheDirectory(String directoryPath) throws VersionControlPluginException {
        // Not needed
    }

    @Override
    public void validate() throws ValidationException {
        if(repository == null){
            throw new ValidationException("No repository specified, you need to specify a repository before calling the validate method");
        }
        File f = new File(repository.getUrl());
        if(!f.exists() || !f.isDirectory()){
            throw new ValidationException("Directory specified as a filesystem repository is not valid");
        }
    }
}
