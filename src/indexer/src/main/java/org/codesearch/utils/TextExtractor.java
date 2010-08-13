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
public class TextExtractor {

    /** Read the contents of the given file. */
    public static String readTextFile(File file, String encoding ) throws IOException {
        
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
    public static String readTextFile(File file ) throws IOException {

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
}
