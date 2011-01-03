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

package com.uwyn.jhighlight.pcj.util;

/**
 *  This class provides static methods for display of collection
 *  elements. It is only provided as a utility class for the collection
 *  implementations and is not a part of the API.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.2     21-08-2003 20:25
 */
public class Display
{
	
    public static String display(boolean v)
	{
        return String.valueOf(v);
    }
	
    public static String display(byte v)
	{
        return String.valueOf(v);
    }
	
    public static String display(short v)
	{
        return String.valueOf(v);
    }
	
    public static String display(int v)
	{
        return String.valueOf(v);
    }
	
    public static String display(long v)
	{
        return String.valueOf(v);
    }
	
    public static String display(float v)
	{
        return String.valueOf(v);
    }
	
    public static String display(double v)
	{
        return String.valueOf(v);
    }
	
    public static String display(char v)
	{
        return "'"+(displayChars.indexOf(v)!=-1? String.valueOf(v) :hexChar(v))+"'";
    }
	
    private static final String displayChars =
	"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!\"#�%&/()=?\'@�${[]}+|^~*-_.:,;<>\\";
	
    static String hexChar(char v)
	{
        String s = Integer.toHexString(v);
        switch (s.length())
		{
			case 1: return "\\u000"+s;
			case 2: return "\\u00"+s;
			case 3: return "\\u0"+s;
			case 4: return "\\u"+s;
			default:
				throw new RuntimeException("Internal error");
        }
    }
	
}
