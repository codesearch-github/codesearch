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

import com.uwyn.jhighlight.highlighter.CppHighlighter;
import com.uwyn.jhighlight.highlighter.ExplicitStateHighlighter;

/**
 * Generates highlighted syntax in XHTML from Cpp source.
 *
 * @author Arnout Engelen (arnouten[remove] at bzzt dot net)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 0$
 */
public class CppXhtmlRenderer extends XhtmlRenderer {

    @Override
    protected String getCssClass(int style) {
        switch (style) {
            case CppHighlighter.PLAIN_STYLE:
                return "cpp_plain";
            case CppHighlighter.KEYWORD_STYLE:
                return "cpp_keyword";
            case CppHighlighter.TYPE_STYLE:
                return "cpp_type";
            case CppHighlighter.OPERATOR_STYLE:
                return "cpp_operator";
            case CppHighlighter.SEPARATOR_STYLE:
                return "cpp_separator";
            case CppHighlighter.LITERAL_STYLE:
                return "cpp_literal";
            case CppHighlighter.CPP_COMMENT_STYLE:
                return "cpp_comment";
            case CppHighlighter.DOXYGEN_COMMENT_STYLE:
                return "cpp_doxygen_comment";
            case CppHighlighter.DOXYGEN_TAG_STYLE:
                return "cpp_doxygen_tag";
            case CppHighlighter.PREPROC_STYLE:
                return "cpp_preproc";
        }

        return null;
    }

    @Override
    protected ExplicitStateHighlighter getHighlighter() {
        return new CppHighlighter();
    }
}
