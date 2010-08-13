/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Stiboller Stephan
 */
public class IndexLogger {

    /** File in which all logging information is stored  */
    private File loggingFile;

    /** log4j Logger */
    private static Log log = LogFactory.getLog(IndexLogger.class);

    /** Date formatter. */
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    /** The file writer to access the log file */
    private FileWriter fw;

    /**
     * Constructor and stuff...
     * @param path is the path where the loggin file should be created/used
     */
    public IndexLogger(final String path) {
        this.loggingFile = new File(path);

        if (!this.loggingFile.exists()) {
            try {
                loggingFile.createNewFile();
                 fw = new FileWriter(loggingFile, true);
            } catch (IOException ioex) {
                log.error("Failed to create Loggin file! " + ioex.getMessage());
            }
        }

    }

    /**
     * Appends a new entry to the logging file.
     * @param entry logged information
     */
    public void append(final String entry) {
         try {
            fw.write("<b>" + simpleDateFormat.format(new Date()) + "</b>: " + entry + "\n");
            fw.flush();
            fw.close();
        } catch (IOException ioe) {
            log.error("Failed writing logging-entry. " + ioe.getMessage());
        }
    }
}
