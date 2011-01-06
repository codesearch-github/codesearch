/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.utils.git;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.codesearch.commons.plugins.vcs.FileDto;
import org.codesearch.commons.utils.MimeTypeUtil;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.IndexDiff;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectStream;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.FooterKey;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.PathFilterGroup;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.eclipse.jgit.util.FS;

/**
 *
 * @author zeheron
 */
public class GitUtils {

    private static GitUtils instance;
    private static final Logger LOG = Logger.getLogger(GitUtils.class);

    private GitUtils() {
    }

    /**
     * Retrieves the singleton instance to use.
     */
    public static GitUtils getInstance() {
        if (instance == null) {
            instance = new GitUtils();
        }
        return instance;
    }

    /**
     * Retrieves the last commit and extracts the revision.
     * @param repo  target repository
     * @return revision number
     * @throws AmbiguousObjectException
     * @throws IOException
     */
    public int retrieveHeadRevisionForRepository(Repository repo) throws AmbiguousObjectException, IOException {
        RevWalk rw = new RevWalk(repo);
        RevCommit c = null;
        AnyObjectId headId = repo.resolve(Constants.HEAD);
        RevCommit root = rw.parseCommit(headId);
        rw.markStart(root);
        c = rw.next();
        rw.dispose();
        LOG.debug("HeadRevision: " + c.getParentCount());
        return c.getParentCount();
    }

    /**
     * Retrieves a single FileDto from the repository
     * @param repo
     * @param targetPath
     * @return FileDto
     * @throws AmbiguousObjectException
     * @throws MissingObjectException
     * @throws IOException
     */
    public FileDto retrieveFile(Repository repo, String targetPath, int revision) throws AmbiguousObjectException, MissingObjectException, IOException {
        RevWalk rw = new RevWalk(repo);
        AnyObjectId headId = repo.resolve(Constants.HEAD);
        //RevTree headTree = rw.parseTree(headId);
        // Start of TreeWalk logic
        final FileTreeIterator workTree = new FileTreeIterator(repo);
        final TreeWalk walk = new TreeWalk(repo);
        //DirCache cache = DirCache.read(repo.getDirectory(), FS.DETECTED);  //could be needed for faster performance
        walk.reset(); // drops the first empty tree
        walk.setRecursive(true);
        walk.addTree(workTree);
        //TODO: improve performance
        while (walk.next()) {
            String path = walk.getPathString();
            if (path.equalsIgnoreCase(targetPath)) {
                ObjectLoader ldr = repo.open(walk.getObjectId(revision), Constants.OBJ_BLOB);
                ObjectStream in = ldr.openStream();
                byte[] tmp = new byte[1024];
                while ((in.read(tmp)) > 0) {
                }
                in.close();
                return new FileDto(path, tmp, isBinary(tmp)); //TODO: mime type
            }
        }
        rw.dispose();
        return new FileDto();
    }

    /**
     * test method
     * @param repository
     * @param FileDto target file
     * @return target file
     * @throws AmbiguousObjectException
     * @throws IOException
     */
    public FileDto retrieveFileM2(Repository repository, String targetPath) throws AmbiguousObjectException, IOException {
        ObjectId head = repository.resolve("HEAD");
        ObjectId id = ObjectId.fromString(Constants.HEAD);
        ObjectLoader ldr = repository.open(id, Constants.OBJ_BLOB);
        byte[] tmp = new byte[1024];
        ObjectStream in = ldr.openStream();
        while ((in.read(tmp)) > 0) {
        }
        in.close();
        return new FileDto(targetPath, tmp, false); //TODO: mime type
    }

    /**
     * Loads the file in specified revision.
     * @param repo
     * @param revision
     * @return null if the file does not exist in given revision
     * @throws IOException
     */
    public List<FileDto> getChangedFilesSinceRevision(Repository repo, String revision) throws IOException {
        final RevWalk rw = new RevWalk(repo);
        final TreeWalk tw = new TreeWalk(repo);
        final int headRevision = retrieveHeadRevisionForRepository(repo);
        List<FileDto> changedFiles = new ArrayList<FileDto>();
        RevTree headTree = rw.parseTree(repo.resolve(Constants.HEAD));
        RevTree specTree = headTree;
        tw.setFilter(TreeFilter.ANY_DIFF);
        ObjectId from = repo.resolve(Constants.HEAD);
        if (from == null) {
            return null;
        }
        rw.markStart(rw.parseCommit(from));

        for (RevCommit rev : rw) {
            if (rev.getParentCount() == Integer.valueOf(revision)) //Parent count is taken as revision number
            {
                specTree = rev.getTree();
            }
        }

        RevTree[] trees = new RevTree[2];
        trees[0] = headTree;
        trees[1] = specTree;
        tw.addTree(headTree);
        tw.addTree(specTree);
        tw.reset(trees);
        while (tw.next()) {
            if (tw.getFileMode(headRevision).getObjectType() == Constants.OBJ_BLOB) {
                LOG.debug("TreeWalk: " + tw.getPathString());
                ObjectLoader ldr = repo.open(tw.getObjectId(headRevision), Constants.OBJ_BLOB);
                ObjectStream in = ldr.openStream();
                byte[] tmp = new byte[1024];
                while ((in.read(tmp)) > 0) {
                }
                in.close();
                changedFiles.add(new FileDto(revision, tmp, isBinary(tmp)));
            }
            if (tw.isSubtree()) {
                tw.enterSubtree();
            }
        }

        return changedFiles;

    }

    /**
     * Binary check method
     * @return true if the buffer is almost certainly binary.
     * Note: Non-ASCII based encoding encoded text is binary,
     * newlines cannot be reliably detected.
     */
    public static boolean isBinary(byte[] buffer) {
        for (int i = 0; i < buffer.length; i++) {
            int ch = buffer[i];
            if (ch < 32 && ch != '\t' && ch != '\n' && ch != '\r') {
                return true;
            }
        }
        return false;
    }
}
