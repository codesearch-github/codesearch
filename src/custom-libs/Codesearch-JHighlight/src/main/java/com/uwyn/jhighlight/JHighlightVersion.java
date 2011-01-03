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

package com.uwyn.jhighlight;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Provides acces to the version number of this JHighlight release.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3108 $
 * @since 1.0
 */
public class JHighlightVersion
{
	private String  mVersion = null;
	
	JHighlightVersion()
	{
		URL version_url = getClass().getClassLoader().getResource("JHIGHLIGHT_VERSION");
		if (version_url != null)
		{
			try
			{
				URLConnection connection = version_url.openConnection();
				connection.setUseCaches(false);
				InputStream inputStream = connection.getInputStream();
				
				byte[]                  buffer = new byte[64];
				int                     return_value = -1;
				ByteArrayOutputStream   output_stream = new ByteArrayOutputStream(buffer.length);
				
				try
				{
					return_value = inputStream.read(buffer);
					
					while (-1 != return_value)
					{
						output_stream.write(buffer, 0, return_value);
						return_value = inputStream.read(buffer);
					}
				}
				finally
				{
					output_stream.close();
					inputStream.close();
				}
				
				mVersion = output_stream.toString("UTF-8");
			}
			catch (IOException e)
			{
				mVersion = null;
			}
		}
		
		if (mVersion != null)
		{
			mVersion = mVersion.trim();
		}
		if (null == mVersion)
		{
			mVersion = "(unknown version)";
		}
	}
	
	private String getVersionString()
	{
		return mVersion;
	}
	
	/**
	 * Returns the version number of this JHighlight release.
	 * 
	 * @return the version number
	 * @since 1.0
	 */
	public static String getVersion()
	{
		return JHighlightVersionSingleton.INSTANCE.getVersionString();
	}
}
