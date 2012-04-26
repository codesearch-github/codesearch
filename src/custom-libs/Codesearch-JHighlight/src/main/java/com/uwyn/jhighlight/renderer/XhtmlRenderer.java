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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import com.uwyn.jhighlight.highlighter.ExplicitStateHighlighter;
import com.uwyn.jhighlight.tools.StringUtils;

/**
 * Provides an abstract base class to perform source code to XHTML syntax
 * highlighting.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @author Samuel Kogler
 */
public abstract class XhtmlRenderer implements Renderer {

    /** {@inheritDoc} */
    @Override
    public String highlight(String input, String beginEscapeToken, String endEscapeToken) throws IOException {
        ExplicitStateHighlighter highlighter = getHighlighter();

        BufferedReader r = new BufferedReader(new StringReader(input));
        StringBuilder out = new StringBuilder();
        String trimmedStartToken = beginEscapeToken.trim();
        String trimmedEndToken = endEscapeToken.trim();
        String line;
        String token;
        int length;
        int style;
        String css_class;
        int previous_style = 0;
        boolean newline = false;
        boolean escaped = false;
        while ((line = r.readLine()) != null) {
            line += "\n";
            line = StringUtils.convertTabsToSpaces(line, 4);

            // should be optimized by reusing a custom LineReader class
            Reader lineReader = new StringReader(line);
            highlighter.setReader(lineReader);
            int index = 0;

            while (index < line.length()) {
                style = highlighter.getNextToken();

                length = highlighter.getTokenLength();
                token = line.substring(index, index + length);
                boolean append = true;
                if (token.startsWith(trimmedStartToken)) {
                    escaped = true;
                    out.deleteCharAt(out.length() - 1);
                    token = token.substring(14);
                    if (token.length() == 0) {
                        append = false;
                    }
                } else if (escaped) {
                    if (token.startsWith(trimmedEndToken)) {
                        escaped = false;
                        out.deleteCharAt(out.length() - 1);
                        token = token.substring(12);
                        if (token.length() == 0) {
                            append = false;
                        }
                    } else {
                        out.append(token);
                        append = false;
                    }
                }

                if (append) {
                    if (style != previous_style || newline) {
                        css_class = getCssClass(style);

                        if (css_class != null) {
                            if (previous_style != 0 && !newline) {
                                out.append("</span>");
                            }
                            out.append("<span class=\"").append(css_class).append("\">");

                            previous_style = style;
                        }
                    }
                    newline = false;
                    out.append(StringUtils.encodeHtml(token));
                }
                index += length;
            }
            out.append("</span>");
            newline = true;
        }
        return out.toString();
    }

    /**
     * Looks up the CSS class identifier that corresponds to the syntax style.
     *
     * @param style The syntax style.
     * @return The requested CSS class identifier; or
     * <p><code>null</code> if the syntax style isn't supported.
     * @since 1.0
     */
    protected abstract String getCssClass(int style);

    /**
     * Returns the language-specific highlighting lexer that should be used
     *
     * @return The requested highlighting lexer.
     * @since 1.0
     */
    protected abstract ExplicitStateHighlighter getHighlighter();


}
