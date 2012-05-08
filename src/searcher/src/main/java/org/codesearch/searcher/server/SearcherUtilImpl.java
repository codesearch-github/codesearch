package org.codesearch.searcher.server;

import javax.inject.Inject;

import org.codesearch.commons.configuration.properties.IndexStatusManager;


/**
 * Default implementation of {@link SearcherUtil}
 * @author Samuel Kogler
 */
public class SearcherUtilImpl implements SearcherUtil {

    private DocumentSearcher documentSearcher;
    private IndexStatusManager indexStatusManager;
    
    /**
     * @param documentSearcher
     * @param indexStatusManager
     */
    @Inject
    public SearcherUtilImpl(DocumentSearcher documentSearcher, IndexStatusManager indexStatusManager) {
        this.documentSearcher = documentSearcher;
        this.indexStatusManager = indexStatusManager;
    }



    /** {@inheritDoc} */
    @Override
    public void refreshIndexInformation() throws InvalidIndexException {        
        documentSearcher.refreshIndex();
        indexStatusManager.refresh();
    }

}
