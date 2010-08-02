/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.configreader.xml;

import java.io.FileNotFoundException;
import java.util.HashMap;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import 

/**
 *
 * @author david
 */
public class PropertyManager {
    XMLConfiguration config = new XMLConfiguration();
    public static void readConfig() throws FileNotFoundException{
        throw new NotImplementedException();
    }

    public String getSingleLinePropertyValue(String key){
        return properties.get(key);
    }
}
