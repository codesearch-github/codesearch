package org.codesearch.searcher.server;

import javax.inject.Inject;

import org.codesearch.commons.configuration.properties.PropertiesManager;


/**
 * Default implementation of {@link SearcherUtil}
 * @author Samuel Kogler
 */
public class SearcherUtilImpl implements SearcherUtil {

    private DocumentSearcher documentSearcher;
    private PropertiesManager propertiesManager;
    
    /**
     * @param documentSearcher
     * @param propertiesManager
     */
    @Inject
    public SearcherUtilImpl(DocumentSearcher documentSearcher, PropertiesManager propertiesManager) {
        this.documentSearcher = documentSearcher;
        this.propertiesManager = propertiesManager;
    }



    /** {@inheritDoc} */
    @Override
    public void refreshIndexInformation() throws InvalidIndexException {        
        documentSearcher.refreshIndex();
        propertiesManager.refresh();
    }

}
