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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.highlighting;

import org.codesearch.commons.plugins.Plugin;
import org.codesearch.commons.plugins.PluginBase;

/**
 * Base interface for all Highlighting plugins
 * @author David Froehlich
 */
@PluginBase
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
