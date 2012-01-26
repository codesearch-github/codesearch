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
package org.codesearch.commons.utils.mime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Utility class that provides methods to guess mime types via file endings and stores mime types of most files as
 * constants
 *
 * @author David Froehlich
 */
public class MimeTypeUtil {

    private static final Logger LOG = Logger.getLogger(MimeTypeUtil.class);
    private static final Set<FileType> knownFileTypes = new HashSet<FileType>();

    /** the mime type for UNKNOWN files */
    public static final String UNKNOWN = "UNKNOWN_MIME_TYPE";

    static {
        readDefinitionFile();
    }

    /**
     * guesses the mime type via the file ending
     *
     * @param fileName the file name of the file, the ending is parsed from the fileName
     * @return the mime type or null if no mime type was found for the file
     */
    public static String guessMimeTypeViaFileEnding(final String fileName) {
        if (StringUtils.isEmpty(fileName) || !fileName.contains(".")) {
            return UNKNOWN;
        }
        String ending = fileName.substring(fileName.lastIndexOf('.') + 1);
        for (FileType fileType : knownFileTypes) {
            for (String fileTypeEnding : fileType.getFileEndings()) {
                if (fileTypeEnding.equalsIgnoreCase(ending)) {
                    return fileType.getMime();
                }
            }
        }

        return UNKNOWN;
    }

    /**
     * Checks whether the given mime type is considered a binary (non-human-readable) format.
     *
     * @param mime The given mime
     * @return Whether the mime is considered a binary format.
     */
    public static boolean isBinaryType(final String mime) {

        for (FileType fileType : knownFileTypes) {
            if (fileType.getMime().equals(mime)) {
                return fileType.isBinary();
            }
        }
        // Assume file is binary by default
        return true;
    }

    private static void readDefinitionFile() {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(MimeTypeUtil.class.getResourceAsStream("/mime.types"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                if(!line.startsWith("#"))  {
                    addFileTypeFromString(line);
                }
            }
        } catch (IOException ex) {
            LOG.error("Could not open the mime type config file: \n" + ex);
        }
    }

    /**
     * Parses a file type definition line and adds a corresponding
     * object to the list of known file types.
     * @param definition The file type string representation
     * @return Success
     */
    private static boolean addFileTypeFromString(String definition) {
        definition = definition.trim();
        String[] parts = definition.split("\\s+");
        if (parts.length < 3) // invalid definition
        {
            return false;
        }
        FileType fileType = new FileType();
        fileType.setMime(parts[0]);
        if (parts[1].equals("binary")) {
            fileType.setBinary(true);
        } else if (parts[1].equals("text")) {
            fileType.setBinary(false);
        }

        Set<String> fileEndings = new HashSet<String>();
        for (int i = 2; i < parts.length; i++) {
            fileEndings.add(parts[i]);
        }

        fileType.setFileEndings(fileEndings);
        knownFileTypes.add(fileType);
        LOG.debug("Added mime type: " + fileType.getMime());
        return true;
    }
}
