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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Provides interface to render the source code highlighting.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3108 $
 * @since 1.0
 */
public interface Renderer {

    /**
     * Transforms source code that's provided through an
     * <code>InputStream</code> to highlighted syntax and writes it back to
     * an <code>OutputStream</code>.
     *
     * @param name The name of the source file.
     * @param in The input stream that provides the source code that needs to
     * be transformed.
     * @param out The output stream to which to result should be written.
     * @param encoding The encoding that will be used to read and write the
     * text.
     * @param fragment <code>true</code> if the result should be a fragment;
     * or <code>false</code> if it should be a complete document
     * @see #highlight(String, String, String, boolean)
     * @since 1.0
     */
    public void highlight(String name, InputStream in, OutputStream out, String encoding, boolean fragment) throws IOException;

    /**
     * Transforms source code that's provided through a
     * <code>String</code> to highlighted syntax and returns it as a
     * <code>String</code>.
     *
     * @param name The name of the source file.
     * @param in The input string that provides the source code that needs to
     * be transformed.
     * @param encoding The encoding that will be used to read and write the
     * text.
     * @param fragment <code>true</code> if the result should be a fragment;
     * or <code>false</code> if it should be a complete document
     * @return the highlighted source code as a string
     * @see #highlight(String, InputStream, OutputStream, String, boolean)
     * @since 1.0
     */
    public String highlight(String name, String in, String encoding, boolean fragment) throws IOException;
}
