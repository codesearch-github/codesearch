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
package org.codesearch.commons.plugins.defaulthighlightingplugin;

import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.codesearch.commons.plugins.highlighting.HighlightingPlugin;
import org.codesearch.commons.plugins.highlighting.HighlightingPluginException;

import com.uwyn.jhighlight.renderer.CppXhtmlRenderer;
import com.uwyn.jhighlight.renderer.JavaXhtmlRenderer;
import com.uwyn.jhighlight.renderer.XhtmlRenderer;
import com.uwyn.jhighlight.renderer.XmlXhtmlRenderer;

/**
 * A highlighting plugin used to highlight Java, CPP, XML, and HTML source code.
 * Can also escape image files(JPEG, PNG, BMP, TIFF) to HTML.
 * @author David Froehlich
 */
public class DefaultHighlightingPlugin implements HighlightingPlugin {

    private XhtmlRenderer renderer;
    /** the mime type for JAVA files */
    public static final String JAVA = "text/x-java-source";
    /** the mime type for C header files */
    public static final String C_HEADER = "text/x-chdr";
    /** the mime type for CPP header files */
    public static final String CPP_HEADER = "text/x-c++hdr";
    /** the mime type for C files */
    public static final String C = "text/x-csrc";
    /** the mime type for CPP files */
    public static final String CPP = "text/x-c++src";
    /** the mime type for XML files */
    public static final String XML = "application/xml";
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

    public DefaultHighlightingPlugin() {
    }

    /** {@inheritDoc} */
    @Override
    public String parseToHtml(byte[] content, String mimeType) throws HighlightingPluginException {
        try {
            if (mimeType.equals(JAVA)) {
                renderer = new JavaXhtmlRenderer();
            } else if (mimeType.equals(C)
                    || mimeType.equals(CPP)
                    || mimeType.equals(C_HEADER)
                    || mimeType.equals(CPP_HEADER)) {
                renderer = new CppXhtmlRenderer();
            } else if (mimeType.equals(HTML)) {
                renderer = new XmlXhtmlRenderer();
            } else if (mimeType.equals(XML)) {
                renderer = new XmlXhtmlRenderer();
            } else if (mimeType.equals(PNG)
                    || mimeType.equals(JPEG)
                    || mimeType.equals(BMP)
                    || mimeType.equals(GIF)
                    || mimeType.equals(TIFF)) {
                return escapeImage(content, mimeType);
            } else {
                renderer = new XmlXhtmlRenderer();
            }
            return renderer.highlight(new String(content, "UTF-8"));
        } catch (IOException ex) {
            throw new HighlightingPluginException("Parsing was not successful\n" + ex);
        } catch (NullPointerException ex) {
            throw new HighlightingPluginException("Renderer was not successfully loaded for mime type " + mimeType);
        }
    }

    /**
     * Escapes an image to HTML with the binary content of the file as source
     * @param content the file content
     * @param mime the mime type of the image
     * @return the image as HTML code
     */
    private String escapeImage(byte[] content, String mime) {
        String base64 = Base64.encodeBase64String(content);
        return "<img name='image' alt='image' src='data:" + mime + ";base64," + base64 + "' />";
    }

    /** {@inheritDoc} */
    @Override
    public String getPurposes() {
        String result = JAVA + " "
                + C + " "
                + CPP + " "
                + C_HEADER + " "
                + CPP_HEADER + " "
                + XML + " "
                + HTML + " "
                + PNG + " "
                + JPEG + " "
                + GIF + " "
                + TIFF + " "
                + BMP;
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public String getVersion() {
        return "0.1-SNAPSHOT";
    }

    /** {@inheritDoc} */
    @Override
    public String getEscapeStartToken() {
        return " _begin_"+"µ_escape_"; //The token is split into 2 string so this file can be viewed without the string triggering highlighting plugin
    }

    /** {@inheritDoc} */
    @Override
    public String getEscapeEndToken() {
        return " _end_"+"µ_escape_"; //The token is split into 2 string so this file can be viewed without the string triggering highlighting plugin
    }
}
