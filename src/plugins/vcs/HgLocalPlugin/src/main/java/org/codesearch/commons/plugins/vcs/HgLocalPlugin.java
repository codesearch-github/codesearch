/**
 * Copyright 2010 David Froehlich   <david.froehlich@businesssoftware.at>,
 *                Samuel Kogler     <samuel.kogler@gmail.com>,
 *                Stephan Stiboller <stistc06@htlkaindorf.at>
 *
 * This file is part of Codesearch.
 *
 * Codesearch is free software: you can redistribute it andor modify
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
 * along with Codesearch.  If not, see <http:www.gnu.orglicenses>.
 */
package org.codesearch.commons.plugins.vcs;

import org.codesearch.commons.configuration.dto.NoAuthentication;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.codesearch.commons.configuration.dto.RepositoryDto;
import org.codesearch.commons.validator.ValidationException;

/**
 * A plugin used to access files stored in Mercurial repositories.
 * Checks out repositories to the local filesystem.
 * @author Samuel Kogler
 */
public class HgLocalPlugin implements VersionControlPlugin {

    private static final Logger LOG = Logger.getLogger(HgLocalPlugin.class);
    private File cacheDirectory = new File("/tmp/codesearch/hg");
    private File branchDirectory;
    private RepositoryDto currentRepository;
    private final String HG_BINARY_LOCATION = "/usr/bin/hg";
    private final String HG_DEFAULT_ARGUMENTS = "";
    private ProcessBuilder processBuilder = new ProcessBuilder();

    /**
     * Creates a new instance of the HgLocalPlugin
     */
    public HgLocalPlugin() {
        if (!new File(HG_BINARY_LOCATION).isFile()) {
            LOG.error("Git Binary not found at " + HG_BINARY_LOCATION);
            LOG.error("Plugin will not be usable");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setRepository(RepositoryDto repo) throws VersionControlPluginException {
        currentRepository = repo;
        branchDirectory = new File(cacheDirectory.getAbsolutePath(), currentRepository.getName());
        synchronized (this) {
            processBuilder.directory(branchDirectory);
        }

        if (branchDirectory.isDirectory()) {
            LOG.info("It seems repository " + repo.getName() + " is already cloned locally, trying to pull new changes...");
            try {
                executeHgCommand("pull");
                return;
            } catch (VersionControlPluginException ex) {
                LOG.warn("Existent directory not a valid hg repository, removing...");
            }
        }

        branchDirectory.mkdirs();
        LOG.info("Cloning repository " + repo.getName() + " ...");

        if (repo.getUsedAuthentication() instanceof NoAuthentication) {
            executeHgCommand("clone", repo.getUrl(), branchDirectory.getAbsolutePath());
        } else {
            throw new VersionControlPluginException("Authentication not supported yet.");
        }
    }

    /** {@inheritDoc} */
    @Override
    public FileDto getFileDtoForFileIdentifierAtRevision(FileIdentifier fileIdentifier, String revision) throws VersionControlPluginException {
        if (revision == null || revision.isEmpty()) {
            revision = ".";
        }
        byte[] fileContent = executeHgCommand("cat", "-r " + revision, fileIdentifier.getFilePath());
        String[] logEntry = new String(executeHgCommand("log", "-l 1", "--template \"{node}$$${author}\"", fileIdentifier.getFilePath())).split("$$$");
        String lastRevision = logEntry[0];
        String lastAuthor = logEntry[1];

        FileDto file = new FileDto();
        file.setContent(fileContent);
        file.setRepository(currentRepository);
        file.setFilePath(fileIdentifier.getFilePath());
        file.setLastAlteration(lastRevision);
        file.setLastAuthor(lastAuthor);
        return file;
    }

    /** {@inheritDoc} */
    @Override
    public Set<FileIdentifier> getChangedFilesSinceRevision(String revision) throws VersionControlPluginException {
        Set<FileIdentifier> files = new HashSet<FileIdentifier>();

        if (revision.equals("0")) {
            List<String> output = bytesToStringList(executeHgCommand("manifest"));

            LOG.debug(output.size() + " changed files since revision " + revision);

            FileIdentifier fileIdentifier = null;
            for (String line : output) {
                fileIdentifier = new FileIdentifier();
                fileIdentifier.setFilePath(line);
                fileIdentifier.setRepository(currentRepository);
                files.add(fileIdentifier);
            }
        } else {
            List<String> output = bytesToStringList(executeHgCommand("status", "--rev " + revision));

            LOG.debug(output.size() + " changed files since revision " + revision);

            FileIdentifier fileIdentifier = null;
            for (String line : output) {
                char status = line.charAt(0);
                String path = line.substring(2);
                fileIdentifier = new FileIdentifier();
                fileIdentifier.setRepository(currentRepository);
                fileIdentifier.setFilePath(path);

                if (status == 'R') {
                    fileIdentifier.setDeleted(true);
                }
                files.add(fileIdentifier);
            }
        }

        return files;
    }

    /** {@inheritDoc} */
    @Override
    public String getRepositoryRevision() throws VersionControlPluginException {
        return new String(executeHgCommand("hg log -l 1 --template \"{node}\""));
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getFilesInDirectory(String directoryPath) throws VersionControlPluginException {
        if (directoryPath != null && directoryPath.startsWith("/")) {
            directoryPath = directoryPath.substring(1);
        }

        File absolutePath = new File(branchDirectory, directoryPath);
        if(absolutePath.isDirectory()) {
            return Arrays.asList(absolutePath.list());
        } else {
            throw new VersionControlPluginException("Cannot list files in directory " + directoryPath + ": File does not exist or is not a directory.");
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getPurposes() {
        return "HG";
    }

    /** {@inheritDoc} */
    @Override
    public void setCacheDirectory(String directoryPath) throws VersionControlPluginException {
        this.cacheDirectory = new File(directoryPath);
        if (!cacheDirectory.isDirectory() && cacheDirectory.canWrite()) {
            throw new VersionControlPluginException("Invalid cache directory specified: " + directoryPath);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void validate() throws ValidationException {
        //TODO add validation logic
    }

    private byte[] executeHgCommand(String... arguments) throws VersionControlPluginException {
        try {
            List<String> command = new LinkedList<String>();
            command.add(HG_BINARY_LOCATION);
            command.add(HG_DEFAULT_ARGUMENTS);
            command.addAll(Arrays.asList(arguments));

            Process process = null;

            synchronized (this) {
                processBuilder = new ProcessBuilder();
                processBuilder.directory(branchDirectory);
                processBuilder.command(command);
                LOG.trace("Executing hg command: " + processBuilder.command());
                process = processBuilder.start();

                byte[] output = IOUtils.toByteArray(process.getInputStream());

                process.waitFor();
                if (process.exitValue() != 0) {
                    throw new VersionControlPluginException("Hg returned error code: " + process.exitValue() + "\n   Output: " + IOUtils.toString(process.getErrorStream()));
                }
                cleanupProcess(process);

                return output;
            }
        } catch (InterruptedException ex) {
            throw new VersionControlPluginException("Execution of hg interrupted by operating system");
        } catch (IOException ex) {
            throw new VersionControlPluginException("Error executing hg command: " + ex);
        }
    }

    private void cleanupProcess(Process p) {
        if (p != null) {
            try {
                p.getErrorStream().close();
            } catch (IOException ex) {
            }
            try {
                p.getInputStream().close();
            } catch (IOException ex) {
            }
            try {
                p.getOutputStream().close();
            } catch (IOException ex) {
            }
            p.destroy();
        }
    }

    private List<String> bytesToStringList(byte[] bytes) {
        List<String> lines = new ArrayList<String>();

        if (bytes == null) {
            return lines;
        }

        BufferedReader r = new BufferedReader(
                new InputStreamReader(
                new ByteArrayInputStream(bytes)));

        try {
            try {
                for (String line = r.readLine(); line != null; line = r.readLine()) {
                    lines.add(line);
                }
            } finally {
                r.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return lines;
    }
}
