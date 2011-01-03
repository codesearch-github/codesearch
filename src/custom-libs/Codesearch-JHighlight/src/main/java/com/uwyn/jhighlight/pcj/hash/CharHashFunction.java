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

package com.uwyn.jhighlight.pcj.hash;

/**
 *  This interface represents hash functions from char values
 *  to int values. The int value result is chosen to achieve
 *  consistence with the common
 *  {@link Object#hashCode() hashCode()}
 *  method. The interface is provided to alter the hash functions used
 *  by hashing data structures, like
 *  {@link com.uwyn.rife.pcj.map.CharKeyIntChainedHashMap CharKeyIntChainedHashMap}
 *  or
 *  {@link com.uwyn.rife.pcj.set.CharChainedHashSet CharChainedHashSet}.
 *
 *  @see        DefaultCharHashFunction
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.0     2002/29/12
 *  @since      1.0
 */
public interface CharHashFunction
{
	
    /**
     *  Returns a hash code for a specified char value.
     *
     *  @param      v
     *              the value for which to return a hash code.
     *
     *  @return     a hash code for the specified value.
     */
    int hash(char v);
	
}
