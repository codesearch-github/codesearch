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

package com.uwyn.jhighlight.renderer;

import com.uwyn.jhighlight.highlighter.ExplicitStateHighlighter;
import com.uwyn.jhighlight.highlighter.XmlHighlighter;

/**
 * Generates highlighted syntax in XHTML from XML source.
 * <p><a href="https://rife.dev.java.net">RIFE</a> template tags are also
 * supported and will be clearly highlighted.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3108 $
 * @since 1.0
 */
public class XmlXhtmlRenderer extends XhtmlRenderer {

    @Override
    protected String getCssClass(int style) {
        switch (style) {
            case XmlHighlighter.PLAIN_STYLE:
                return "xml_plain";
            case XmlHighlighter.CHAR_DATA:
                return "xml_char_data";
            case XmlHighlighter.TAG_SYMBOLS:
                return "xml_tag_symbols";
            case XmlHighlighter.COMMENT:
                return "xml_comment";
            case XmlHighlighter.ATTRIBUTE_VALUE:
                return "xml_attribute_value";
            case XmlHighlighter.ATTRIBUTE_NAME:
                return "xml_attribute_name";
            case XmlHighlighter.PROCESSING_INSTRUCTION:
                return "xml_processing_instruction";
            case XmlHighlighter.TAG_NAME:
                return "xml_tag_name";
            case XmlHighlighter.RIFE_TAG:
                return "xml_rife_tag";
            case XmlHighlighter.RIFE_NAME:
                return "xml_rife_name";
        }

        return null;
    }

    @Override
    protected ExplicitStateHighlighter getHighlighter() {
        return new XmlHighlighter();
    }
}
