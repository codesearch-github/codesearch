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
 *  This interface represents iterators over maps from
 *  char values to objects.
 *
 *  @see        com.uwyn.rife.pcj.map.CharKeyMap
 *  @see        com.uwyn.rife.pcj.CharIterator
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.0     2003/6/1
 *  @since      1.0
 */
public interface CharKeyMapIterator
{
	
    /**
     *  Indicates whether more entries can be returned by this
     *  iterator.
     *
     *  @return     <tt>true</tt> if more char entries can be returned
     *              by this iterator; returns <tt>false</tt>
     *              otherwise.
     *
     *  @see        #next()
     */
    boolean hasNext();
	
    /**
     *  Advances to the next entry of this iterator.
     *
     *  @throws     java.util.NoSuchElementException
     *              if no more entries are available from this
     *              iterator.
     *
     *  @see        #hasNext()
     */
    void next();
	
    /**
     *  Removes the last entry value returned from the underlying
     *  map.
     *
     *  @throws     UnsupportedOperationException
     *              if removal is not supported by this iterator.
     *
     *  @throws     IllegalStateException
     *              if no entry has been returned by this iterator
     *              yet.
     */
    void remove();
	
    /**
     *  Returns the key of the current entry of this iterator.
     *
     *  @return     the key of the current entry of this iterator.
     *
     *  @throws     IllegalStateException
     *              if there is no current entry (i.e. if
     *              {@link #next() next()}
     *              has not been called or
     *              {@link #remove() remove()}
     *              has just been called.
     *
     *  @see        #getValue()
     */
    char getKey();
	
    /**
     *  Returns the value of the current entry of this iterator.
     *
     *  @return     the value of the current entry of this iterator
     *              (which may be null).
     *
     *  @throws     IllegalStateException
     *              if there is no current entry (i.e. if
     *              {@link #next() next()}
     *              has not been called or
     *              {@link #remove() remove()}
     *              has just been called.
     *
     *  @see        #getKey()
     */
    Object getValue();
	
}
