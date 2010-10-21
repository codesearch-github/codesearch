/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.highlighting;

/**
 * An exception that can be thrown by an HighlightingPlugin
 * @author David Froehlich
 */
public class HighlightingPluginException extends Exception{
    public HighlightingPluginException(){
        super();
    }

    public HighlightingPluginException(String msg){
        super(msg);
    }
}
