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
import java.util.logging.Level;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.codesearch.commons.configuration.xml.XmlConfigurationReaderConstants;
import org.codesearch.commons.configuration.xml.XmlConfigurationReader;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.constants.IndexConstants;
import org.codesearch.commons.database.DBAccess;
import org.codesearch.commons.database.DatabaseAccessException;
import org.codesearch.indexer.exceptions.TaskExecutionException;
import org.quartz.impl.jdbcjobstore.DB2v6Delegate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Clears either the index of a single repository or deletes the entire index.
 * If the repositoryName is not set, the entire index is cleared.
 * @author David Froehlich
 * @author Samuel Kogler
 */
public class ClearTask implements Task {

    /** the location of the index */
    private String indexLocation;
    /** the name of the repository - if empty or null the entire index is deleted */
    private String repositoryName;
    /** the configuration reader used to retrieve the index location */
    @Autowired
    private XmlConfigurationReader configReader;
    /** Logger */
    private static final Logger LOG = Logger.getLogger(ClearTask.class);

    @Override
    public void execute() throws TaskExecutionException {
        try {
            indexLocation = configReader.getSingleLinePropertyValue(XmlConfigurationReaderConstants.INDEX_LOCATION);

            if (repositoryName.isEmpty()) { // Clear the whole index
                File indexDir = new File(indexLocation);
                boolean indexSuccess = true;
                for (File f : indexDir.listFiles()) {
                    if (!f.delete()) {
                        LOG.error("Could not delete file: " + f.getName());
                        indexSuccess = false;
                    }
                }
                if (indexSuccess) {
                    LOG.debug("Deleted all index files");
                }
                boolean dbSuccess = true;
                try {
                    for (RepositoryDto currentRepo : configReader.getRepositories()) {
                        DBAccess.clearTablesForRepository(currentRepo.getName());
                    }
                } catch (DatabaseAccessException ex) {
                    dbSuccess = false;
                    LOG.debug("Could not delete rows from database, if codesearch is configured not to use code analysis this is perfectly normal, otherwise, check your database connection\n" + ex);
                }
                if (dbSuccess) {
                    LOG.debug("Deleted all rows in the database associated to code analysis");
                }
            } else { // Clear specific repository from the index
                IndexSearcher searcher = new IndexSearcher(FSDirectory.open(new File(indexLocation)), false);
                Term term = new Term(IndexConstants.INDEX_FIELD_REPOSITORY, repositoryName);
                deleteDocumentsFromIndexUsingTerm(term, searcher);
                searcher.close();
                LOG.debug("Deleted " + searcher.getIndexReader().deleteDocuments(term) + " documents with repository " + repositoryName);
                try {
                    DBAccess.clearTablesForRepository(repositoryName);
                    LOG.debug("Deleted all rows from database associated to the repository "+repositoryName);
                } catch (DatabaseAccessException ex) {
                    LOG.debug("Could not delete rows from database, if codesearch is configured not to use code analysis this is perfectly normal, otherwise, check your database connection\n" + ex);
                }
            }
        } catch (ConfigurationException ex) {
            throw new TaskExecutionException("Could not read index location: \n" + ex);
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
    public void deleteDocumentsFromIndexUsingTerm(Term term, IndexSearcher indexSearcher) throws IOException {
        LOG.debug("Deleting documents with field '" + term.field() + "' with text '" + term.text() + "'");
        indexSearcher.getIndexReader().deleteDocuments(term);
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }
}
