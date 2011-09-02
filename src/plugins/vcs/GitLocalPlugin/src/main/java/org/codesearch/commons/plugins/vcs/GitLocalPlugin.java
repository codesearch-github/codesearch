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

import org.codesearch.commons.configuration.xml.dto.RepositoryDto;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
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

/**
 * A plugin used to access files stored in Git repositories.
 * Checks out repositories to the local filesystem.
 * @author Samuel Kogler
 */
public class GitLocalPlugin implements VersionControlPlugin {

    private static final Logger LOG = Logger.getLogger(GitLocalPlugin.class);
    private File cacheDirectory = new File("/tmp/codesearch/git/");
    private File branchDirectory;
    private RepositoryDto currentRepository;
    private final String GIT_BINARY_LOCATION = "/usr/bin/git";
    private final String GIT_DEFAULT_ARGUMENTS = "--no-pager";
    private ProcessBuilder processBuilder = new ProcessBuilder();

    /**
     * Creates a new instance of the GitLocalPlugin
     */
    public GitLocalPlugin() {
        if (!new File(GIT_BINARY_LOCATION).isFile()) {
            LOG.error("Git Binary not found at " + GIT_BINARY_LOCATION);
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
                executeGitCommand("pull");
                return;
            } catch(VersionControlPluginException ex) {
                LOG.warn("Existent directory not a valid git repository, removing...");
            }
        }

        branchDirectory.mkdirs();
        LOG.info("Cloning repository " + repo.getName() + " ...");

        if (repo.getUsedAuthentication() instanceof NoAuthentication) {
            executeGitCommand("clone", repo.getUrl(), branchDirectory.getAbsolutePath());
        } else {
            throw new VersionControlPluginException("Authentication not supported yet.");
        }
    }

    /** {@inheritDoc} */
    @Override
    public FileDto getFileForFilePath(String filePath) throws VersionControlPluginException {
        byte[] fileContent = executeGitCommand("show", "HEAD:" + filePath);
        String[] logEntry = new String(executeGitCommand("log", "-1", "--pretty=\"format:%H$$$%an\"", filePath)).split("$$$");
        String lastRevision = logEntry[0];
        String lastAuthor = logEntry[1];

        FileDto file = new FileDto();
        file.setContent(fileContent);
        file.setRepository(currentRepository);
        file.setFilePath(filePath);
        file.setLastAlteration(lastRevision);
        file.setLastAuthor(lastAuthor);
        return file;
    }

    /** {@inheritDoc} */
    @Override
    public Set<FileDto> getChangedFilesSinceRevision(String revision) throws VersionControlPluginException {
        Set<FileDto> files = new HashSet<FileDto>();

        if (revision.equals("0")) {
            List<String> output = bytesToStringList(executeGitCommand("ls-files"));

            LOG.debug(output.size() + " changed files since commit " + revision);

            for (String line : output) {
                files.add(getFileForFilePath(line));
            }
        } else {
            List<String> output = bytesToStringList(executeGitCommand("diff", "--name-status"));

            LOG.debug(output.size() + " changed files since commit " + revision);

            for (String line : output) {
                char status = line.charAt(0);
                String path = line.substring(2);

                if (status == 'D') {
                    FileDto f = new FileDto();
                    f.setFilePath(path);
                    f.setDeleted(true);
                    files.add(f);
                } else {
                    files.add(getFileForFilePath(path));
                }
            }
        }


        return files;
    }

    /** {@inheritDoc} */
    @Override
    public String getRepositoryRevision() throws VersionControlPluginException {
        return new String(executeGitCommand("rev-parse", "HEAD"));
    }

    @Override
    public List<String> getFilesInDirectory(String directoryPath) throws VersionControlPluginException {
        if (directoryPath != null && directoryPath.startsWith("/")) {
            directoryPath = directoryPath.substring(1);
        }
        List<String> files = new LinkedList<String>();
        List<String> output = bytesToStringList(executeGitCommand("ls-tree", "HEAD" + ":" + directoryPath));

        for (String line : output) {
            String[] cols = line.split("\\s+");
            files.add(cols[3]);
        }

        return files;
    }

    /** {@inheritDoc} */
    @Override
    public String getPurposes() {
        return "GIT";
    }

    /** {@inheritDoc} */
    @Override
    public String getVersion() {
        return "0.1";
    }

    private byte[] executeGitCommand(String... arguments) throws VersionControlPluginException {
        try {
            List<String> command = new LinkedList<String>();
            command.add(GIT_BINARY_LOCATION);
            command.add(GIT_DEFAULT_ARGUMENTS);
            command.addAll(Arrays.asList(arguments));

            Process process = null;

            synchronized (this) {
                processBuilder = new ProcessBuilder();
                processBuilder.directory(branchDirectory);
                processBuilder.command(command);
                LOG.trace("Executing git command: " + processBuilder.command());
                process = processBuilder.start();

                byte[] output = IOUtils.toByteArray(process.getInputStream());

                process.waitFor();
                if (process.exitValue() != 0) {
                    throw new VersionControlPluginException("Git returned error code: " + process.exitValue() + "\n    Git output: " + IOUtils.toString(process.getErrorStream()));
                }
                cleanupProcess(process);

                return output;
            }
        } catch (InterruptedException ex) {
            throw new VersionControlPluginException("Execution of git interrupted by operating system");
        } catch (IOException ex) {
            throw new VersionControlPluginException("Error executing git command: " + ex);
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

    @Override
    public void setCacheDirectory(String directoryPath) throws VersionControlPluginException {
        this.cacheDirectory = new File(directoryPath);
        if (!cacheDirectory.isDirectory() && cacheDirectory.canWrite()) {
            throw new VersionControlPluginException("Invalid cache directory specified: " + directoryPath);
        }
    }
}
