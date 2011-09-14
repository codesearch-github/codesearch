/**
 * 
 */
package org.codesearch.indexer.client.rpc;

import java.util.List;

import org.codesearch.indexer.shared.LogServiceException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Samuel Kogler
 * Retrieves the data for the log page.
 */
@RemoteServiceRelativePath("../log.rpc")
public interface LogService extends RemoteService {

    
    /**
     * Gets the log messages from the server.
     * @return list of log messages.
     */
    List<String> getLog() throws LogServiceException;
}
