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
import com.uwyn.jhighlight.highlighter.JavaHighlighter;

/**
 * Generates highlighted syntax in XHTML from Java source.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3108 $
 * @since 1.0
 */
public class JavaXhtmlRenderer extends XhtmlRenderer {

    @Override
    protected String getCssClass(int style) {
        switch (style) {
            case JavaHighlighter.PLAIN_STYLE:
                return "java_plain";
            case JavaHighlighter.KEYWORD_STYLE:
                return "java_keyword";
            case JavaHighlighter.TYPE_STYLE:
                return "java_type";
            case JavaHighlighter.OPERATOR_STYLE:
                return "java_operator";
            case JavaHighlighter.SEPARATOR_STYLE:
                return "java_separator";
            case JavaHighlighter.LITERAL_STYLE:
                return "java_literal";
            case JavaHighlighter.JAVA_COMMENT_STYLE:
                return "java_comment";
            case JavaHighlighter.JAVADOC_COMMENT_STYLE:
                return "java_javadoc_comment";
            case JavaHighlighter.JAVADOC_TAG_STYLE:
                return "java_javadoc_tag";
        }

        return null;
    }

    @Override
    protected ExplicitStateHighlighter getHighlighter() {
        JavaHighlighter highlighter = new JavaHighlighter();
        JavaHighlighter.ASSERT_IS_KEYWORD = true;

        return highlighter;
    }
}
