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
import com.uwyn.jhighlight.highlighter.GroovyHighlighter;
import java.util.HashMap;
import java.util.Map;

/**
 * Generates highlighted syntax in XHTML from Groovy source.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3108 $
 * @since 1.0
 */
public class GroovyXhtmlRenderer extends XhtmlRenderer {

    public final static HashMap DEFAULT_CSS = new HashMap() {

        {
            put("h1",
                    "font-family: sans-serif; "
                    + "font-size: 16pt; "
                    + "font-weight: bold; "
                    + "color: rgb(0,0,0); "
                    + "background: rgb(210,210,210); "
                    + "border: solid 1px black; "
                    + "padding: 5px; "
                    + "text-align: center;");

            put("code",
                    "color: rgb(0,0,0); "
                    + "font-family: monospace; "
                    + "font-size: 12px; "
                    + "white-space: nowrap;");

            put(".java_plain",
                    "color: rgb(0,0,0);");

            put(".java_keyword",
                    "color: rgb(0,0,0); "
                    + "font-weight: bold;");

            put(".java_type",
                    "color: rgb(0,44,221);");

            put(".java_operator",
                    "color: rgb(0,124,31);");

            put(".java_separator",
                    "color: rgb(0,33,255);");

            put(".java_literal",
                    "color: rgb(188,0,0);");

            put(".java_comment",
                    "color: rgb(147,147,147); "
                    + "background-color: rgb(247,247,247);");

            put(".java_javadoc_comment",
                    "color: rgb(147,147,147); "
                    + "background-color: rgb(247,247,247); "
                    + "font-style: italic;");

            put(".java_javadoc_tag",
                    "color: rgb(147,147,147); "
                    + "background-color: rgb(247,247,247); "
                    + "font-style: italic; "
                    + "font-weight: bold;");
        }
    };

    @Override
    protected Map getDefaultCssStyles() {
        return DEFAULT_CSS;
    }

    @Override
    protected String getCssClass(int style) {
        switch (style) {
            case GroovyHighlighter.PLAIN_STYLE:
                return "java_plain";
            case GroovyHighlighter.KEYWORD_STYLE:
                return "java_keyword";
            case GroovyHighlighter.TYPE_STYLE:
                return "java_type";
            case GroovyHighlighter.OPERATOR_STYLE:
                return "java_operator";
            case GroovyHighlighter.SEPARATOR_STYLE:
                return "java_separator";
            case GroovyHighlighter.LITERAL_STYLE:
                return "java_literal";
            case GroovyHighlighter.JAVA_COMMENT_STYLE:
                return "java_comment";
            case GroovyHighlighter.JAVADOC_COMMENT_STYLE:
                return "java_javadoc_comment";
            case GroovyHighlighter.JAVADOC_TAG_STYLE:
                return "java_javadoc_tag";
        }

        return null;
    }

    @Override
    protected ExplicitStateHighlighter getHighlighter() {
        return new GroovyHighlighter();
    }
}
