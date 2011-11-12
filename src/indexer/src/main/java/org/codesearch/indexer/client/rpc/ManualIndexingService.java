/**
 * 
 */
package org.codesearch.indexer.client.rpc;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import java.util.List;
import org.codesearch.indexer.shared.ManualIndexingData;
import org.codesearch.indexer.shared.ManualIndexingServiceException;

/**
 * @author Samuel Kogler
 * The service used the retrieve the data for the manual indexing job
 */
@RemoteServiceRelativePath("../manualIndexing.rpc")
public interface ManualIndexingService extends RemoteService {
	
	/**
	 * Retrieves the data for the manual indexing job
	 * @return The data for the manual indexing job.
	 */
	ManualIndexingData getData() throws ManualIndexingServiceException;
        void startManualIndexing(List<String> repositories, List<String> repositoryGroups, boolean clear) throws ManualIndexingServiceException;
}