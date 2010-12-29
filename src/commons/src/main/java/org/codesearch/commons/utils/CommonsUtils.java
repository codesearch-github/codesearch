/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.utils;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
    public static String getMimeTypeForFile(byte[] bArray) {
        Collection<MimeType> col = MimeUtil.getMimeTypes(bArray);
        return ((MimeType) col.toArray()[0]).toString();
    }

    /**
     * Converts a file to a byte[]
     * @param file
     * @return byte[] file content
     * @throws Exception
     */
    public static byte[] convertFileToByteArray(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte fileContent[] = new byte[(int) file.length()];
        fis.read(fileContent);
        return fileContent;

//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        FileInputStream fileInputStream = new FileInputStream(file);
//        byte[] buffer = new byte[16384];
//        for (int len = fileInputStream.read(buffer); len > 0; len = fileInputStream.read(buffer)) {
//            byteArrayOutputStream.write(buffer, 0, len);
//        }
//        fileInputStream.close();
//        return byteArrayOutputStream.toByteArray();
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
