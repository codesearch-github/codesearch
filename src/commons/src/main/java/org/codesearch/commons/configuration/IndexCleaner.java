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
package org.codesearch.commons.configuration;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.codesearch.commons.configuration.dto.RepositoryDto;
import org.codesearch.commons.configuration.properties.IndexStatusManager;
import org.codesearch.commons.constants.IndexConstants;
import org.codesearch.commons.database.DBAccess;
import org.codesearch.commons.database.DatabaseAccessException;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;

/**
 * Cleans the index of all data associated with invalid repositories
 *
 * @author David Froehlich
 */
public class IndexCleaner {

    private IndexStatusManager indexStatusManager;
    private ConfigurationReader configReader;
    private DBAccess dbAccess;
    public static Logger LOG = Logger.getLogger(IndexCleaner.class);

    public IndexCleaner(IndexStatusManager indexStatusManager, ConfigurationReader configReader, DBAccess dbAccess) {
        this.indexStatusManager = indexStatusManager;
        this.configReader = configReader;
        this.dbAccess = dbAccess;
    }

    public void cleanIndex() throws InvalidConfigurationException {
        // delete all lucene documents and database information associated to a repository that exists in the configuration
        // in case the repository configuraiton has changed since the last startup
        Directory indexDirectory;
        IndexWriter indexWriter = null;
        try {
            indexDirectory = FSDirectory.open(configReader.getIndexLocation());
            Analyzer analyzer = new StandardAnalyzer(IndexConstants.LUCENE_VERSION);
            IndexWriterConfig config = new IndexWriterConfig(IndexConstants.LUCENE_VERSION, analyzer);
            indexWriter = new IndexWriter(indexDirectory, config);

            List<String> invalidRepositories = indexStatusManager.getAllSavedRepositoryNames();

            BooleanQuery query = new BooleanQuery();
            for (RepositoryDto repository : configReader.getRepositories()) {
                invalidRepositories.remove(repository.getName());
            }
            for (String invalidRepo : invalidRepositories) {
                dbAccess.deleteRepository(invalidRepo);
                if (!indexStatusManager.getStatus(invalidRepo).equals(VersionControlPlugin.UNDEFINED_VERSION)) {
                    Query q = new TermQuery(new Term("repository", invalidRepo));
                    query.add(q, BooleanClause.Occur.SHOULD);
                    indexStatusManager.setStatus(invalidRepo, VersionControlPlugin.UNDEFINED_VERSION);
                }
            }
            
            int countDocuments = indexWriter.maxDoc();
            indexWriter.deleteDocuments(query);
            indexWriter.commit();
            int countDeleted = countDocuments - indexWriter.maxDoc();
            if (countDeleted > 0) {
                LOG.info("Deleted " + countDeleted + " documents from the Index since they belonged to repositories that were no longer found in the configuration.");
            }
        } catch (DatabaseAccessException ex) {
            throw new InvalidConfigurationException("Could not clean analysis data for unused repository" + ex);
        } catch (IOException ex) {
            throw new InvalidConfigurationException("Index can not be opened for writing at: " + configReader.getIndexLocation() + "\n" + ex);
        } finally {
            try {
                indexWriter.close();
            } catch (IOException ex) {
                LOG.error("Unable to close IndexWriter after cleaning up Lucene index\n" + ex);
            }
        }
    }
}
