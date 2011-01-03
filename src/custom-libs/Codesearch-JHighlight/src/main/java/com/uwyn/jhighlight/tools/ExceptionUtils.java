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

package com.uwyn.jhighlight.tools;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Collection of utility methods to work with exceptions.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3108 $
 * @since 1.0
 */
public abstract class ExceptionUtils
{
	private ExceptionUtils()
	{
	}
	
	/**
	 * Obtains the entire stracktrace of an exception and converts it into a
	 * string.
	 * 
	 * @param exception the exception whose stacktrace has to be converted
	 * @return the stracktrace, converted into a string
	 * @since 1.0
	 */
	public static String getExceptionStackTrace(Throwable exception)
	{
		if (null == exception)  throw new IllegalArgumentException("exception can't be null;");
		
		String stack_trace = null;
		
		StringWriter string_writer = new StringWriter();
		PrintWriter print_writer = new PrintWriter(string_writer);
		
		exception.printStackTrace(print_writer);
		
		stack_trace = string_writer.getBuffer().toString();
		
		print_writer.close();
		
		try
		{
			string_writer.close();
		}
		// JDK 1.2.2 compatibility
		catch (Throwable e2)
		{
		}
		
		return stack_trace;
	}
}

