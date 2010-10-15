package org.codesearch.indexer.tasks;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import org.apache.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.codesearch.commons.configuration.xml.XmlConfigurationReader;
import org.codesearch.commons.constants.IndexConstants;
import org.codesearch.indexer.exceptions.TaskExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * If repository is set, clears the index of all fields of that repository,
 * otherwise deletes the entire index.
 * The indexLocation must be set before executing this task.
 * 
 * @author David Froehlich
 */
public class ClearTask extends Task {
    /** logger. **/
    private static final Logger LOG = Logger.getLogger(ClearTask.class);
    /** the searcher used to find the fields of the repository in the index */
    private IndexSearcher searcher;

    @Override
    public void execute() throws TaskExecutionException {
        if(indexLocation == null) {
            throw new TaskExecutionException("Index directory not set before executing task!");
        }
        if (repository == null) {
            File indexDir = new File(indexLocation);
            for (File f : indexDir.listFiles()) {
                if (!f.delete()) {
                    LOG.error("Could not delete file in index-directory: " + f.getName());
                }
            }
        } else {
            try {
                searcher = new IndexSearcher(FSDirectory.open(new File(indexLocation)), false);
                Term term = new Term(IndexConstants.INDEX_FIELD_REPOSITORY, repository.getName());
                deleteDocumentsFromIndexUsingTerm(term);
                LOG.debug("Deleted " + searcher.getIndexReader().deleteDocuments(term) + " documents with repository " + repository.getName());
            } catch (CorruptIndexException ex) {
                LOG.error("CorruptedIndex: " + ex);
            } catch (IOException ex) {
                LOG.error("IOException while trying to open the index" + ex);
            }
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
