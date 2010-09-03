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

package org.codesearch.indexer.core;

import java.io.File;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println("Executing Indexing...");

        File pathToTestFiles = new File("/home/david/workspace/test");
        File pathToIndexDir = new File("/tmp/test/");
        IndexerCore iCore = new IndexerCore();


        iCore.initializeIndexWriter(new StandardAnalyzer(Version.LUCENE_30),pathToIndexDir );
        //iCore.cleanIndexDirectory(pathToIndexDir);
        boolean lol = iCore.indexDirectory(pathToTestFiles);


        System.out.println( "Bla.. Bla.. Bla.. ->"  + lol);
        System.out.println("3 x Bla means: WIN ");
    }
}
