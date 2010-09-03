/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.searcher.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.lucene.queryParser.ParseException;
import org.codesearch.searcher.shared.ResultItem;

/**
 *
 * @author david
 */
public class test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            DocumentSearcher searcher = new DocumentSearcher();
            try {
                for(ResultItem ri : searcher.getResultsForSearch("codesearch")){
                    System.out.println(ri.getRepository());
                    System.out.println(ri.getFilePath());
                    System.out.println(ri.getRelevance());
                }

            } catch (ParseException ex) {
                Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ConfigurationException ex) {
            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}
