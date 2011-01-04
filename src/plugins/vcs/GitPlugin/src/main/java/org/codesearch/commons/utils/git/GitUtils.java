/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.utils.git;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.codesearch.commons.plugins.vcs.FileDto;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
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
    public FileDto retrieveFile(Repository repo, String targetPath) throws AmbiguousObjectException, MissingObjectException, IOException {
        RevWalk rw = new RevWalk(repo);
        AnyObjectId headId = repo.resolve(Constants.HEAD);
        RevTree headTree = rw.parseTree(headId);
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
                return new FileDto(path, walk.getRawPath(), false); //TODO: mime type
            }
        }
        rw.dispose();
        return new FileDto();
    }

    /**
     * test method
     * @param repository
     * @param targetPath
     * @return
     * @throws AmbiguousObjectException
     * @throws IOException
     */
    public FileDto retrieveFileM2(Repository repository, String targetPath) throws AmbiguousObjectException, IOException {
        ObjectId head = repository.resolve("HEAD");
        ObjectId id = ObjectId.fromString(Constants.HEAD);
        ObjectLoader ldr = repository.open(id, Constants.OBJ_BLOB);
        byte[] tmp = new byte[1024];
        ObjectStream in = ldr.openStream();
        int n;
        while ((n = in.read(tmp)) > 0) {}
        in.close();
        return new FileDto(targetPath, tmp, false); //TODO: mime type
    }
}
