/**
 * Copyright 2010 David Froehlich   <david.froehlich@businesssoftware.at>,
 *                Samuel Kogler     <samuel.kogler@gmail.com>,
 *                Stephan Stiboller <stistc06@htlkaindorf.at>
 *
 * This file is part of Codesearch.
 *
 * Codesearch is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Codesearch is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Codesearch.  If not, see <http://www.gnu.org/licenses/>.
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
    public static Log log = LogFactory.getLog(IndexLogger.class);

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
     * Clears the log file content
     */
    public boolean clearLog()
    {
        return FileTool.clearFileContent(loggingFile);
    }

    /**
     * Appends a new entry to the logging file.
     * @param entry logged information
     */
    public void append(final String entry) {
         try {
            fw = new FileWriter(loggingFile, true);
            fw.write(simpleDateFormat.format(new Date()) + ": " + entry + "\n");
            fw.flush();
            fw.close();
        } catch (IOException ioe) {
            log.error("Failed writing logging-entry. " + ioe.getMessage());
        }
    }
}
