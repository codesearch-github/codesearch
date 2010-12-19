/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.highlighting;

import org.codesearch.commons.plugins.Plugin;

/**
 * Base interface for all Highlighting plugins
 * @author David Froehlich
 */
public interface HighlightingPlugin extends Plugin {

    /**
     * Parses the content to HTML code escaping all HTML characters (<, >)
     * @param content the content to be parsed
     * @param mimeType the mimeType of the file
     * @return the HTML-save and highlighted code
     */
    String parseToHtml(byte[] content, String mimeType) throws HighlightingPluginException;

    /**
     * returns the string token that is used to mark the beginning of an escape part in the codes that will not be converted to html
     * @return the escape token
     */
    String getEscapeStartToken();

    /**
     * returns the string token that is used to mark the end of an escape part in the codes that will not be converted to html
     * @return the escape token
     */
    String getEscapeEndToken();
}
