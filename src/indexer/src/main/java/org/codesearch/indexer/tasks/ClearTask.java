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
package org.codesearch.indexer.tasks;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.codesearch.commons.configuration.properties.PropertiesManager;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.constants.IndexConstants;
import org.codesearch.commons.database.DBAccess;
import org.codesearch.commons.database.DatabaseAccessException;
import org.codesearch.indexer.exceptions.TaskExecutionException;

/**
 * Clears either the index of a single repository or deletes the entire index.
 * If the repositoryName is not set, the entire index is cleared.
 * @author David Froehlich
 * @author Samuel Kogler
 */
public class ClearTask implements Task {

    /** Whether code analysis is enabled. */
    private boolean codeAnalysisEnabled;
    /** the location of the index */
    private String indexLocation;
    /** the repository to clear  */
    private RepositoryDto repository;
    /** Logger */
    private static final Logger LOG = Logger.getLogger(ClearTask.class);

    /**
     * executes the task
     * deletes all index files and resets the lastIndexingRevision of all repositories
     * @throws TaskExecutionException
     */
    @Override
    public void execute() throws TaskExecutionException {
        try {
            if (repository == null) { // Clear the whole index
                File indexDir = new File(indexLocation);
                boolean deleteSuccess = true;
                for (File f : indexDir.listFiles()) {
                    if (!f.delete()) {
                        LOG.error("Could not delete file: " + f.getName());
                        deleteSuccess = false;
                    }
                }
                if (deleteSuccess) {
                    LOG.info("Successfully cleared index");
                } else {
                    LOG.error("Could not delete all index files");
                }
                if (codeAnalysisEnabled) {
                    try {
                        DBAccess.resetLastAnalyzedRevision();
                        LOG.info("Cleared code analysis index");
                    } catch (DatabaseAccessException ex) {
                        LOG.error("Could not clear code analysis index: \n" + ex);
                    }
                }
            } else { // Clear specific repository from the index
                IndexSearcher searcher = new IndexSearcher(FSDirectory.open(new File(indexLocation)), false);
                Term term = new Term(IndexConstants.INDEX_FIELD_REPOSITORY, repository.getName());
                deleteDocumentsFromIndexUsingTerm(term, searcher);
                searcher.close();

                PropertiesManager propertiesManager = new PropertiesManager(indexLocation + IndexConstants.REVISIONS_PROPERTY_FILENAME);
                propertiesManager.setPropertyFileValue(repository.getName(), "0");

                LOG.debug("Deleted " + searcher.getIndexReader().deleteDocuments(term) + " documents for repository " + repository.getName());
                if (codeAnalysisEnabled) {
                    try {
                        DBAccess.clearTablesForRepository(repository.getName());
                        DBAccess.setLastAnalyzedRevisionOfRepository(repository.getName(), "0");
                        LOG.info("Cleared code analysis index for repository " + repository.getName());
                    } catch (DatabaseAccessException ex) {
                        LOG.error("Could not clear code analysis index: \n" + ex);
                    }
                }
            }
        } catch (CorruptIndexException ex) {
            throw new TaskExecutionException("Corrupt index: \n" + ex);
        } catch (IOException ex) {
            throw new TaskExecutionException("IOException while trying to access the index: \n" + ex);
        }
    }

    /**
     * Removes all documents related to the used Term
     * @param term The term
     * @throws IOException
     */
    private void deleteDocumentsFromIndexUsingTerm(Term term, IndexSearcher indexSearcher) throws IOException {
        LOG.debug("Deleting documents with field '" + term.field() + "' with text '" + term.text() + "'");
        indexSearcher.getIndexReader().deleteDocuments(term);
    }

    @Override
    public void setIndexLocation(String indexLocation) {
        this.indexLocation = indexLocation;
    }

    @Override
    public void setRepository(RepositoryDto repository) {
        this.repository = repository;
    }

    @Override
    public void setCodeAnalysisEnabled(boolean codeAnalysisEnabled) {
        this.codeAnalysisEnabled = codeAnalysisEnabled;
    }
}
