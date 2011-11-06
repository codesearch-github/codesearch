/**
 *
 */
package org.codesearch.indexer.server.rpc;

import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.codesearch.indexer.server.manager.IndexingManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.LinkedList;
import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.configuration.dto.RepositoryDto;
import org.codesearch.indexer.client.rpc.ManualIndexingService;
import org.codesearch.indexer.shared.ManualIndexingData;
import org.codesearch.indexer.shared.ManualIndexingServiceException;
import org.quartz.SchedulerException;

/**
 * @author Samuel Kogler
 *
 */
@Singleton
public class ManualIndexingServiceImpl extends RemoteServiceServlet implements ManualIndexingService {

    private ConfigurationReader configReader;
    /** . */
    private static final long serialVersionUID = 8656390076402009245L;
    /** The indexing manager used to query the job status. */
    private IndexingManager indexingManager;

    @Inject
    public ManualIndexingServiceImpl(IndexingManager indexingManager, ConfigurationReader configReader) {
        this.configReader = configReader;
        this.indexingManager = indexingManager;
    }

    /** {@inheritDoc} */
    @Override
    public ManualIndexingData getData() throws ManualIndexingServiceException {
        List<String> repositories = new LinkedList<String>();
        for (RepositoryDto repo : configReader.getRepositories()) {
            repositories.add(repo.getName());
        }
        List<String> repositoryGroups = configReader.getRepositoryGroups();
        ManualIndexingData data = new ManualIndexingData(repositories, repositoryGroups);
        return data;
    }

    @Override
    public void startManualIndexing(List<String> repositories, List<String> repositoryGroups) throws ManualIndexingServiceException {
        try {
            indexingManager.startJobForRepositories(repositories, repositoryGroups);
        } catch (SchedulerException ex) {
            throw new ManualIndexingServiceException("Could not start manual indexing job:\n"+ex);
        }
    }
}
