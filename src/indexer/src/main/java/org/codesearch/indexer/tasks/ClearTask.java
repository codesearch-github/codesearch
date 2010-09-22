/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.indexer.tasks;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.codesearch.commons.configreader.xml.PropertyManager;
import org.codesearch.indexer.exceptions.TaskExecutionException;

/**
 *
 * @author David Froehlich
 */
public class ClearTask implements Task {

    private String indexLocation;
    private String repositoryName;
    private IndexSearcher searcher;
    private PropertyManager propertyManager;
    private static final Logger LOG = Logger.getLogger(ClearTask.class);


    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    @Override
    public void execute() throws TaskExecutionException {
        try {
            if (indexLocation.equals("")) {
                indexLocation = propertyManager.getSingleLinePropertyValue("index_location");
            }
            searcher = new IndexSearcher(FSDirectory.open(new File(indexLocation)), false);
            Term term = new Term("REPOSITORY", repositoryName);
            deleteDocumentsFromIndexUsingTerm(term);
            LOG.debug("Deleted " + searcher.getIndexReader().deleteDocuments(term) + " documents with repository " + repositoryName);
        } catch (CorruptIndexException ex) {
            LOG.error("CorruptedIndex: " + ex);
        } catch (IOException ex) {
            LOG.error("IOException while trying to open the index" + ex);
        } catch (ConfigurationException ex) {
            LOG.error("Could not retrieve value for index_location from configuration" + ex);
        }
    }

    /**
     * Removes all documents related to the used Term
     * @param term
     * @throws IOException
     * @throws ParseException
     */
    public void deleteDocumentsFromIndexUsingTerm(Term term) throws IOException {
		LOG.info("Deleting documents with field '" + term.field() + "' with text '" + term.text() + "'");
		searcher.getIndexReader().deleteDocuments(term);
                searcher.close();
	}
}
