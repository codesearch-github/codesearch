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

package com.uwyn.jhighlight.highlighter;

import java.io.IOException;
import java.io.Reader;

/**
 * Provides access to the lexical scanning of a highlighted language.
 * 
 * @author Omnicore Software
 * @author Hans Kratz &amp; Dennis Strein GbR
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3108 $
 * @since 1.0
 */
public interface ExplicitStateHighlighter
{
	/**
	 * Sets the reader that will be used to receive the text data.
	 * 
	 * @param reader the <code>Reader</code> that has to be used
	 */
	void setReader(Reader reader);

	/**
	 * Obtain the next token from the scanner.
	 * 
	 * @return one of the tokens that are define in the scanner
	 * @exception IOException when an error occurred during the parsing of
	 * the reader
	 */
	byte getNextToken() throws IOException;

	/**
	 * Returns the length of the matched text region.
	 * 
	 * @return the length of the matched text region
	 */
	int getTokenLength();
}
