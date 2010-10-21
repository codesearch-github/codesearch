/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.indexer.core;

import java.io.ByteArrayOutputStream;
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
 *
 * @author zeheron
 */
public class IndexerUtils {

    /**
     * This method retrieves the mime type of the specified file
     * @param filePath
     * @return mime type
     * @throws VersionControlPluginException
     */
    public static String getMimeTypeForFile(ByteArrayOutputStream baos){
         try {
            MagicMatch match = Magic.getMagicMatch(baos.toByteArray());
            return match.getMimeType();
        } catch (MagicParseException ex) {
            Logger.getLogger(SubversionPlugin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MagicMatchNotFoundException ex) {
            Logger.getLogger(SubversionPlugin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MagicException ex) {
            Logger.getLogger(SubversionPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "No mime type found";
    }

}
