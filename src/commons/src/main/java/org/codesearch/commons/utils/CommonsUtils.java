/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.utils;

import eu.medsea.mimeutil.MimeUtil;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;
import org.codesearch.commons.plugins.vcs.SubversionPlugin;
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
        Collection<String> col = MimeUtil.getMimeTypes(baos.toByteArray());
        return (String) col.toArray()[0];
    }

    /**
     * This method retrieves the mime type of the specified file
     * @param filePath
     * @return mime type
     * @throws VersionControlPluginException
     */
    public static Collection<String> getMimeTypesForFile(ByteArrayOutputStream baos) {
        Collection<String> col = MimeUtil.getMimeTypes(baos.toByteArray());
        return col;
    }
}
