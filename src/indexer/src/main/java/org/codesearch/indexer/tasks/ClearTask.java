package org.codesearch.indexer.tasks;

import java.io.File;
import java.io.IOException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.codesearch.commons.configuration.xml.XmlConfigurationReaderConstants;
import org.codesearch.commons.configuration.xml.XmlConfigurationReader;
import org.codesearch.indexer.exceptions.TaskExecutionException;

/**
 * clears either the lucene index of all fields of a single repository or deletes the entire index
 * @author David Froehlich
 */
public class ClearTask implements Task {

    /** the location of the index */
    private String indexLocation = "";
    /** the name of the repository whichs fields should be cleared or null if the entire index is to be deleted */
    private String repositoryName;
    /** the searcher used to find the fields of the repository in the index */
    private IndexSearcher searcher;
    /** the config reader used to retrieve the index location */
    private XmlConfigurationReader configReader = new XmlConfigurationReader();

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
                indexLocation = configReader.getSingleLinePropertyValue(XmlConfigurationReaderConstants.INDEX_LOCATION);
            }
        } catch (ConfigurationException ex) {
            System.out.println("Could not retrieve value for index_location from configuration" + ex);
        }
        if (repositoryName == null) {
            File indexDir = new File(indexLocation);
            for (File f : indexDir.listFiles()) {
                if (!f.delete()) {
                    System.out.println("Could not delete file in index-directory: " + f.getName());
                }
            }
        } else {
            try {

                searcher = new IndexSearcher(FSDirectory.open(new File(indexLocation)), false);
                Term term = new Term("REPOSITORY", repositoryName);
                deleteDocumentsFromIndexUsingTerm(term);
                System.out.println("Deleted " + searcher.getIndexReader().deleteDocuments(term) + " documents with repository " + repositoryName);
            } catch (CorruptIndexException ex) {
                System.out.println("CorruptedIndex: " + ex);
            } catch (IOException ex) {
                System.out.println("IOException while trying to open the index" + ex);
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
        System.out.println("Deleting documents with field '" + term.field() + "' with text '" + term.text() + "'");
        searcher.getIndexReader().deleteDocuments(term);
        searcher.close();
    }
}
