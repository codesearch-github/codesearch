/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Util class that provides methods to guess mime types via file endings and stores mime types of most files as constants
 * @author David Froehlich
 */
public class MimeTypeUtil {

    /** the mime type for JAVA files */
    public static final String JAVA = "text/x-java-source";
    /** the mime type for CPP files */
    public static final String CPP = "text/x-c";
    /** the mime type for XML files */
    public static final String XML = "text/xml";
    /** the mime type for HTML files */
    public static final String HTML = "text/html";
    /** the mime type for PNG files */
    public static final String PNG = "image/png";

    /** stores all known file endings and the appropriate mime type */
    private final static Map<String, String> MIME_TYPES = new HashMap<String, String>() {{
            put("java", JAVA);
            put("cpp", CPP);
            put("xml", XML);
            put("html", HTML);
            put("png", PNG);
        }
    };

    /**
     * guesses the mime type via the file ending
     * if an entry with the key = fileEnding is in the map mimeTypes the value is returned
     * @param fileName the file name of the file, the ending is parsed from the fileName
     * @return the mime type or null if no mime type was found for the file
     */
    public static String guessMimeTypeViaFileEnding(final String fileName) {
        String fileEnding = fileName.substring(fileName.lastIndexOf(".")+1);
        return MIME_TYPES.get(fileEnding.toLowerCase());
    }
}
