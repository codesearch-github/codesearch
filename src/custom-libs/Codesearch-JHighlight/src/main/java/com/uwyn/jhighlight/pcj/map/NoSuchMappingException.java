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

package com.uwyn.jhighlight.pcj.map;

/**
 *  Thrown to indicate that an attempt was made to retrieve a
 *  non-existing mapping in a map.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.0     2002/30/12
 *  @since      1.0
 */
public class NoSuchMappingException extends RuntimeException
{
	
    /**
     *  Creates a new exception with a specified detail message.
     *  The message indicates the key of the mapping that was
     *  not available.
     *
     *  @param      s
     *              the detail message.
     *
     *  @throws     NullPointerException
     *              if <tt>s</tt> is <tt>null</tt>.
     */
    public NoSuchMappingException(String s)
	{
        super(s);
    }
	
}
