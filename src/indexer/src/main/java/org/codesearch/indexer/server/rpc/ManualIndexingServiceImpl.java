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
/**
 *
 */
package org.codesearch.indexer.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.configuration.dto.RepositoryDto;
import org.codesearch.indexer.client.rpc.ManualIndexingService;
import org.codesearch.indexer.server.manager.IndexingManager;
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
    public void startManualIndexing(List<String> repositories, List<String> repositoryGroups, boolean clear) throws ManualIndexingServiceException {
        try {
            indexingManager.startJobForRepositories(repositories, repositoryGroups, clear);
        } catch (SchedulerException ex) {
            throw new ManualIndexingServiceException("Could not start manual indexing job:\n"+ex);
        }
    }
}
