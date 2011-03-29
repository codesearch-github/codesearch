/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.searcher.server.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.codesearch.searcher.server.DocumentSearcher;
import org.codesearch.searcher.server.InvalidIndexException;

/**
 * servlet that is called from the indexer when the index in the searcher has to be refreshed
 * @author David Froehlich
 */
@Singleton
public class UpdateIndexerServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(UpdateIndexerServlet.class);
    private DocumentSearcher documentSearcher;

    @Inject
    public UpdateIndexerServlet(DocumentSearcher documentSearcher) {
        this.documentSearcher = documentSearcher;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            documentSearcher.refreshIndex();
            LOG.debug("Updated index for searcher");
        } catch (InvalidIndexException ex) {
            LOG.error("Refreshing of index in searcher was not successful\n" + ex);
        }
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
