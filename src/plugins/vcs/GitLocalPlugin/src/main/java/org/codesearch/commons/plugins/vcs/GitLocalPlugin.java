/**
 * Copyright 2010 David Froehlich <david.froehlich@businesssoftware.at>, Samuel
 * Kogler <samuel.kogler@gmail.com>, Stephan Stiboller <stistc06@htlkaindorf.at>
 *
 * This file is part of Codesearch.
 *
 * Codesearch is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Codesearch is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Codesearch. If not, see <http://www.gnu.org/licenses/>.
 */
package org.codesearch.commons.plugins.vcs;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codesearch.commons.configuration.dto.BasicAuthentication;
import org.codesearch.commons.configuration.dto.NoAuthentication;
import org.codesearch.commons.configuration.dto.RepositoryDto;
import org.codesearch.commons.configuration.dto.SshAuthentication;
import org.codesearch.commons.validator.ValidationException;

/**
 * A plugin used to access files stored in Git repositories. Checks out
 * repositories to the local filesystem.
 *
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
    private String sshWrapperCommandTemplate = "/usr/bin/ssh-agent /bin/bash -c 'ssh-add %1s; %2s'";

    /**
     * Creates a new instance of the GitLocalPlugin
     */
    public GitLocalPlugin() {
        if (!new File(GIT_BINARY_LOCATION).isFile()) {
            LOG.error("Git Binary not found at " + GIT_BINARY_LOCATION);
            LOG.error("Plugin will not be usable");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRepository(RepositoryDto repo) throws VersionControlPluginException {
        currentRepository = repo;
        branchDirectory = new File(cacheDirectory.getAbsolutePath(), currentRepository.getName());
        synchronized (this) {
            processBuilder.directory(branchDirectory);
        }

        if (branchDirectory.isDirectory()) {
            LOG.info("Repository " + repo.getName() + " already exists locally.");
        } else {
            branchDirectory.mkdirs();
            LOG.info("Cloning repository " + repo.getName() + " ...");
            if (repo.getUsedAuthentication() instanceof NoAuthentication) {
                executeGitCommand("clone", repo.getUrl(), branchDirectory.getAbsolutePath());
            } else if (repo.getUsedAuthentication() instanceof BasicAuthentication) {
                BasicAuthentication ba = (BasicAuthentication) repo.getUsedAuthentication();
                String repoUrl = repo.getUrl();
                if (repoUrl.startsWith("https://")) {
                    repoUrl = repoUrl.replace("://", "://" + ba.getUsername() + ":" + ba.getPassword() + "@");
                } else {
                    throw new VersionControlPluginException("Unsupported protocol for basic authentication.");
                }
                executeGitCommand("clone", repoUrl, branchDirectory.getAbsolutePath());
            } else if (repo.getUsedAuthentication() instanceof SshAuthentication) {
                SshAuthentication sa = (SshAuthentication) repo.getUsedAuthentication();
                String gitCloneCommand = GIT_BINARY_LOCATION + " " + GIT_DEFAULT_ARGUMENTS + " clone "
                        + repo.getUrl() + " " + branchDirectory.getAbsolutePath();
                String cloneCommandSSH = String.format(sshWrapperCommandTemplate, sa.getSshFilePath(), gitCloneCommand);
                executeCommand("/bin/bash", "-c", cloneCommandSSH);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileDto getFile(FileIdentifier fileIdentifier, String revision) throws VersionControlPluginException, VcsFileNotFoundException {
        revision = parseRevision(revision);
        String gitIdentifier = revision + ":" + fileIdentifier.getFilePath();
        //Check if file exists
        if (!checkFile(gitIdentifier)) {
            throw new VcsFileNotFoundException("File " + fileIdentifier + "@" + revision + " does not exist. Try pulling new changes.");
        }

        byte[] fileContent = executeGitCommand("show", gitIdentifier);
        String lastRevision = StringUtils.chomp(new String(executeGitCommand("log", revision, "-n1", "--pretty=%H", fileIdentifier.getFilePath())));
        String lastAuthor = StringUtils.chomp(new String(executeGitCommand("log", revision, "-n1", "--pretty=%an", fileIdentifier.getFilePath())));

        FileDto file = new FileDto();
        file.setContent(fileContent);
        file.setRepository(currentRepository);
        file.setFilePath(fileIdentifier.getFilePath());
        file.setLastAlteration(lastRevision);
        file.setLastAuthor(lastAuthor);
        return file;
    }

    /**
     * {@inheritDoc} WARNING: The GitLocalPlugin does not support
     * black-/whitelist patterns when retrieving changed files, these lists will
     * simply be ignored
     */
    @Override
    public Set<FileIdentifier> getChangedFilesSinceRevision(String revision, List<String> blacklistPatterns, List<String> whitelistPatterns) throws VersionControlPluginException {
        Set<FileIdentifier> files = new HashSet<FileIdentifier>();

        if (revision.equals(VersionControlPlugin.UNDEFINED_VERSION)) {
            List<String> output = bytesToStringList(executeGitCommand("ls-files"));

            LOG.debug(output.size() + " changed files since commit " + revision);

            FileIdentifier fileIdentifier = null;
            for (String line : output) {
                fileIdentifier = new FileIdentifier();
                fileIdentifier.setFilePath(line);
                fileIdentifier.setRepository(currentRepository);
                files.add(fileIdentifier);
            }
        } else {
            List<String> output = bytesToStringList(executeGitCommand("diff", revision, "--name-status"));

            LOG.debug(output.size() + " changed files since commit " + revision);

            FileIdentifier fileIdentifier = null;
            for (String line : output) {
                char status = line.charAt(0);
                String path = line.substring(2);
                fileIdentifier = new FileIdentifier();
                fileIdentifier.setRepository(currentRepository);
                fileIdentifier.setFilePath(path);

                if (status == 'D') {
                    fileIdentifier.setDeleted(true);
                }
                files.add(fileIdentifier);
            }
        }

        return files;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRepositoryRevision() throws VersionControlPluginException {
        return StringUtils.chomp(new String(executeGitCommand("rev-parse", "HEAD")));
    }

    @Override
    public List<String> getFilesInDirectory(String directoryPath, String revision) throws VersionControlPluginException {
        revision = parseRevision(revision);
        if (directoryPath != null && directoryPath.startsWith("/")) {
            directoryPath = directoryPath.substring(1);
        }
        List<String> files = new LinkedList<String>();
        List<String> output = bytesToStringList(executeGitCommand("ls-tree", revision + ":" + directoryPath));

        for (String line : output) {
            String[] cols = line.split("\\s+");
            files.add(cols[3]);
        }

        return files;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPurposes() {
        return "GIT";
    }

    @Override
    public void setCacheDirectory(String directoryPath) throws VersionControlPluginException {
        this.cacheDirectory = new File(directoryPath);
        if (!(cacheDirectory.isDirectory() && cacheDirectory.canWrite())) {
            throw new VersionControlPluginException("Invalid cache directory specified: " + directoryPath);
        }
    }

    @Override
    public void validate() throws ValidationException {
        //TODO add validation logic
    }

    private byte[] executeGitCommand(String... arguments) throws VersionControlPluginException {
        List<String> cmd = new LinkedList<String>();
        cmd.add(GIT_BINARY_LOCATION);
        cmd.add(GIT_DEFAULT_ARGUMENTS);
        Collections.addAll(cmd, arguments);
        return executeCommand(cmd.toArray(new String[0]));
    }

    private byte[] executeCommand(String... commands) throws VersionControlPluginException {
        try {
            Process process = null;

            synchronized (this) {
                processBuilder = new ProcessBuilder();
                processBuilder.directory(branchDirectory);
                processBuilder.command(commands);
                LOG.debug("Executing command: " + processBuilder.command());
                process = processBuilder.start();

                byte[] output = IOUtils.toByteArray(process.getInputStream());

                process.waitFor();
                if (process.exitValue() != 0) {
                    throw new VersionControlPluginException("Command returned error code: " + process.exitValue() + "\n    Output: " + IOUtils.toString(process.getErrorStream()));
                }
                cleanupProcess(process);

                return output;
            }
        } catch (InterruptedException ex) {
            throw new VersionControlPluginException("Execution of command interrupted by operating system");
        } catch (IOException ex) {
            throw new VersionControlPluginException("Error executing command: " + ex);
        }
    }

    private String parseRevision(String revision) {
        if (revision == null || revision.isEmpty() || revision.equals(VersionControlPlugin.UNDEFINED_VERSION)) {
            return "HEAD";
        } else {
            return revision;
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

    private boolean checkFile(String gitIdentifier) {
        try {
            executeGitCommand("rev-parse", "--verify", gitIdentifier);
            return true;
        } catch (VersionControlPluginException ex) {
            return false;
        }
    }

    @Override
    public void pullChanges() throws VersionControlPluginException {
        try {
            if (currentRepository.getUsedAuthentication() instanceof SshAuthentication) {
                SshAuthentication sa = (SshAuthentication) currentRepository.getUsedAuthentication();
                String gitPullCommand = GIT_BINARY_LOCATION + " " + GIT_DEFAULT_ARGUMENTS + " pull";
                String pullCommandSSH = String.format(sshWrapperCommandTemplate, sa.getSshFilePath(), gitPullCommand);
                executeCommand("/bin/bash", "-c", pullCommandSSH);
            } else {
                executeGitCommand("pull");
            }
        } catch (VersionControlPluginException ex) {
            throw new VersionControlPluginException("Pulling new changes failed: \n" + ex);
        }
    }
}
