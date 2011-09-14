/**
 * 
 */
package org.codesearch.indexer.server.rpc;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.codesearch.indexer.client.rpc.LogService;
import org.codesearch.indexer.server.manager.IndexingManager;
import org.codesearch.indexer.shared.LogServiceException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author Samuel Kogler
 * 
 */
@Singleton
public class LogServiceImpl extends RemoteServiceServlet implements LogService {

    /** . */
    private IndexingManager indexingManager;

    @Inject
    public LogServiceImpl(IndexingManager indexingManager) {
        this.indexingManager = indexingManager;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getLog() throws LogServiceException {
        return indexingManager.getLog();
    }

}
