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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * Collection of utility methods to work with files.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3108 $
 * @since 1.0
 */
public abstract class FileUtils
{
	private FileUtils()
	{
	}

	/**
	 * Recursively traverse a directory hierachy and obtain a list of all
	 * absolute file names.
	 * <p>Regular expression patterns can be provided to explicitly include
	 * and exclude certain file names.
	 * 
	 * @param file the directory whose file hierarchy will be traversed
	 * @param included an array of regular expression patterns that will be
	 * used to determine which files should be included; or
	 * <p><code>null</code> if all files should be included
	 * @param excluded an array of regular expression patterns that will be
	 * used to determine which files should be excluded; or
	 * <p><code>null</code> if no files should be excluded
	 * @return the list of absolute file names
	 * @since 1.0
	 */
	public static ArrayList getFileList(File file, Pattern[] included, Pattern[] excluded)
	{
		return getFileList(file, included, excluded, true);
	}
	
	private static ArrayList getFileList(File file, Pattern[] included, Pattern[] excluded, boolean root)
	{
		if (null == file)
		{
			return new ArrayList();
		}
		
		ArrayList filelist = new ArrayList();
		if (file.isDirectory())
		{
			String[] list = file.list();
			if (null != list)
			{
				String list_entry;
				for (int i = 0; i < list.length; i++)
				{
					list_entry = list[i];
					
					File next_file = new File(file.getAbsolutePath() + File.separator + list_entry);
					ArrayList dir = getFileList(next_file, included, excluded, false);
					
					Iterator dir_it = dir.iterator();
					String file_name;
					while (dir_it.hasNext())
					{
						file_name = (String)dir_it.next();
						
						if (root)
						{
							// if the file is not accepted, don't process it further
							if (!StringUtils.filter(file_name, included, excluded))
							{
								continue;
							}
							
						}
						else
						{
							file_name = file.getName() + File.separator + file_name;
						}
						
						int filelist_size = filelist.size();
						for (int j = 0; j < filelist_size; j++)
						{
							if (((String)filelist.get(j)).compareTo(file_name) > 0)
							{
								filelist.add(j, file_name);
								break;
							}
						}
						if (filelist.size() == filelist_size)
						{
							filelist.add(file_name);
						}
					}
				}
			}
		}
		else if (file.isFile())
		{
			String  file_name = file.getName();
			
			if (root)
			{
				if (StringUtils.filter(file_name, included, excluded))
				{
					filelist.add(file_name);
				}
			}
			else
			{
				filelist.add(file_name);
			}
		}
		
		return filelist;
	}
	
	public static String getExtension(String fileName)
	{
		if (null == fileName)	throw new IllegalArgumentException("fileName can't be null.");
		
		String	ext = null;
		
		int	index = fileName.lastIndexOf('.');
		if (index > 0 &&  index < fileName.length() - 1)
		{
			ext = fileName.substring(index+1).toLowerCase();
		}
		
		return ext;
	}
}
