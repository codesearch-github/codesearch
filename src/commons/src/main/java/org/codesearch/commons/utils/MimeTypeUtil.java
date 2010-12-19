package org.codesearch.commons.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class that provides methods to guess mime types via file endings and stores mime types of most files as constants
 * @author David Froehlich
 */
public class MimeTypeUtil {

    /** the mime type for JAVA files */
    public static final String JAVA = "text/x-java-source";
    /** the mime type for CPP header files */
    public static final String CPP_HEADER = "text/x-h";
    /** the mime type for CPP files */
    public static final String CPP = "text/x-c";
    /** the mime type for XML files */
    public static final String XML = "text/xml";
    /** the mime type for HTML files */
    public static final String HTML = "text/html";
    /** the mime type for PNG files */
    public static final String PNG = "image/png";
    /** the mime type for JPEG files */
    public static final String JPEG = "image/jpeg";
    /** the mime type for TIFF files */
    public static final String TIFF = "image/tiff";
    /** the mime type for GIF files */
    public static final String GIF = "image/gif";
    /** the mime type for BMP files */
    public static final String BMP = "image/bmp";
    /** the mime type for UNKNOWN files */
    public static final String UNKNOWN = "unknown";


    /** stores all known file endings and the appropriate mime type */
    private final static Map<String, String> MIME_TYPES = new HashMap<String, String>() {

        {
            put("java", JAVA);
            put("cpp", CPP);
            put("h", CPP_HEADER);
            put("xml", XML);
            put("html", HTML);
            put("png", PNG);
            put("jpg", JPEG);
            put("jpe", JPEG);
            put("jpeg", JPEG);
            put("tif", TIFF);
            put("tiff", TIFF);
            put("gif", GIF);
            put("bmp", BMP);
        }
    };

    /**
     * guesses the mime type via the file ending
     * if an entry with the key = fileEnding is in the map mimeTypes the value is returned
     * @param fileName the file name of the file, the ending is parsed from the fileName
     * @return the mime type or null if no mime type was found for the file
     */
    public static String guessMimeTypeViaFileEnding(final String fileName) {
        String fileEnding = fileName.substring(fileName.lastIndexOf(".") + 1);
        String mime = MIME_TYPES.get(fileEnding.toLowerCase());
        if(mime == null) {
            mime = UNKNOWN;
        }
        return mime;
    }
}
