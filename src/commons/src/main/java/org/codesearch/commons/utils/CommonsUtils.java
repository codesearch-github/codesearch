/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.utils;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import org.codesearch.commons.plugins.vcs.VersionControlPluginException;

/**
 * @author Stephan Stiboller
 * @author David Froehlich
 */
public class CommonsUtils {

    /**
     * This method retrieves the mime type of the specified file
     * @param filePath
     * @return mime type
     * @throws VersionControlPluginException
     */
    public static String getMimeTypeForFile(ByteArrayOutputStream baos) {
        Collection<MimeType> col = MimeUtil.getMimeTypes(baos.toByteArray());
        return ((MimeType) col.toArray()[0]).toString();
    }

    /**
     * This method retrieves the mime type of the specified file
     * @param filePath
     * @return mime type
     * @throws VersionControlPluginException
     */
    public static Collection<MimeType> getMimeTypesForFile(ByteArrayOutputStream baos) {
        Collection<MimeType> col = MimeUtil.getMimeTypes(baos.toByteArray());
        return col;
    }
}
