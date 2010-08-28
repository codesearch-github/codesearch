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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.utils;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * This class is used to extract the text
 * out of a text file.
 * @author zeheron
 */
public final class FileTool {

    /**
     * Deletes a directory recursivly
     * @param path
     * @return true if sucessful
     */
    static public boolean deleteDirectory(final File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    /** Read the contents of the given file. */
    public static String readTextFile(final File file, final String encoding) throws IOException {

        StringBuilder text = new StringBuilder();
        String NL = System.getProperty("line.separator");
        Scanner scanner = new Scanner(file, encoding);
        try {
            while (scanner.hasNextLine()) {
                text.append(scanner.nextLine() + NL);
            }
        } finally {
            scanner.close();
        }
        return text.toString();

    }

    /** Read the contents of the given file. */
    public static String readTextFile(final File file) throws IOException {

        StringBuilder text = new StringBuilder();
        String NL = System.getProperty("line.separator");
        Scanner scanner = new Scanner(file);
        try {
            while (scanner.hasNextLine()) {
                text.append(scanner.nextLine() + NL);
            }
        } finally {
            scanner.close();
        }
        return text.toString();

    }

    /**
     * Clear 
     * @param file
     * @return
     */
    public static boolean clearFileContent(File file)
    {
        boolean check = false;
        String path = file.getAbsolutePath();
        check = file.delete();
        file = new File(path);
        return check;
    }
}
