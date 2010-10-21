/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.javahighlightingplugin;

import com.uwyn.jhighlight.renderer.JavaXhtmlRenderer;
import com.uwyn.jhighlight.renderer.XhtmlRenderer;
import java.io.IOException;
import org.codesearch.commons.plugins.highlighting.HighlightingPlugin;
import org.codesearch.commons.plugins.highlighting.HighlightingPluginException;
import org.springframework.stereotype.Component;

/**
 * A highlighting plugin used to highlight java files
 * @author David Froehlich
 */
@Component
public class JavaHighlightingPlugin implements HighlightingPlugin {

    XhtmlRenderer renderer = new JavaXhtmlRenderer();

    @Override
    public String parseToHtml(String text) throws HighlightingPluginException {
        try {
            return renderer.highlight("test", text, "UTF8", true);
        } catch (IOException ex) {
            throw new HighlightingPluginException("Parsing was not successful\n" + ex);
        }
    }

    @Override
    public String getPurpose() {
        return "JAVA";
    }

    @Override
    public String getVersion() {
        return "0.1_SNAPSHOT";
    }

    @Override
    public String getEscapeStartToken() {
        return "begin_escape";
    }

    @Override
    public String getEscapeEndToken() {
        return "end_escape";
    }
}
