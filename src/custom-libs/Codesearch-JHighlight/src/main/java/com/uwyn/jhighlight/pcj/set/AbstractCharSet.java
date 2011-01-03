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

package com.uwyn.jhighlight.pcj.set;

import com.uwyn.jhighlight.pcj.AbstractCharCollection;
import com.uwyn.jhighlight.pcj.CharIterator;
import com.uwyn.jhighlight.pcj.hash.DefaultCharHashFunction;
import com.uwyn.jhighlight.pcj.set.CharSet;

/**
 *  This class represents an abstract base for implementing
 *  sets of char values. All operations that can be implemented
 *  using iterators and the <tt>get()</tt> and <tt>set()</tt> methods
 *  are implemented as such. In most cases, this is
 *  hardly an efficient solution, and at least some of those
 *  methods should be overridden by sub-classes.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.1     2003/1/10
 *  @since      1.0
 */
public abstract class AbstractCharSet extends AbstractCharCollection implements CharSet
{
	
    /** Default constructor to be invoked by sub-classes. */
    protected AbstractCharSet()
	{ }
	
    public boolean equals(Object obj)
	{
        if (!(obj instanceof CharSet))
            return false;
        CharSet s = (CharSet)obj;
        if (s.size()!=size())
            return false;
        return containsAll(s);
    }
	
    public int hashCode()
	{
        int h = 0;
        CharIterator i = iterator();
        while (i.hasNext())
            h += DefaultCharHashFunction.INSTANCE.hash(i.next());
        return h;
    }
	
}
