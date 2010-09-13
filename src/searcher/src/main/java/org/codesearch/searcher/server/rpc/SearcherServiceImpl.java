/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.searcher.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;

import org.codesearch.searcher.client.rpc.SearcherService;
import org.codesearch.searcher.server.DocumentSearcher;
import org.codesearch.searcher.shared.SearchResultDto;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Samuel Kogler
 */
public class SearcherServiceImpl extends RemoteServiceServlet implements SearcherService {
    /**
     * The document searcher used to search tho index.
     */
    private DocumentSearcher documentSearcher;

     /** The logger. */
    private static final Logger LOG = Logger.getLogger(SearcherServiceImpl.class);


    @Override
    public SearchResultDto[] doSearch(String query) {
        List<SearchResultDto> resultItems = new LinkedList<SearchResultDto>();
        try {
            // Do something interesting with 's' here on the server.
            resultItems = documentSearcher.search(query);
        } catch (ParseException ex) {
            LOG.error("Could not parse query: " + ex);
        } catch (IOException ex) {
            LOG.error(ex);
        }
        return resultItems.toArray(new SearchResultDto[0]);
    }

    public DocumentSearcher getDocumentSearcher() {
        return documentSearcher;
    }

    public void setDocumentSearcher(DocumentSearcher documentSearcher) {
        this.documentSearcher = documentSearcher;
    }
}
