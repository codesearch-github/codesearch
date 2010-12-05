/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.defaulthighlightingplugin;

import com.uwyn.jhighlight.renderer.CppXhtmlRenderer;
import com.uwyn.jhighlight.renderer.JavaXhtmlRenderer;
import com.uwyn.jhighlight.renderer.XhtmlRenderer;
import com.uwyn.jhighlight.renderer.XmlXhtmlRenderer;
import java.io.IOException;
import org.codesearch.commons.constants.MimeTypeNames;
import org.codesearch.commons.plugins.highlighting.HighlightingPlugin;
import org.codesearch.commons.plugins.highlighting.HighlightingPluginException;
import org.springframework.stereotype.Component;

/**
 * A highlighting plugin used to highlight java files
 * @author David Froehlich
 */
@Component
public class DefaultHighlightingPlugin implements HighlightingPlugin {

    XhtmlRenderer renderer;

    @Override
    public String parseToHtml(String text, String mimeType) throws HighlightingPluginException {
        try {
            if (mimeType.equals(MimeTypeNames.JAVA)) {
                renderer = new JavaXhtmlRenderer();
            } else if (mimeType.equals(MimeTypeNames.CPP)) {
                renderer = new CppXhtmlRenderer();
            } else if (mimeType.equals(MimeTypeNames.HTML)) {
                //       renderer = new XhtmlRenderer() {}
                //TODO implement HTML renderer
                renderer = new XmlXhtmlRenderer();
            } else if (mimeType.equals(MimeTypeNames.XML)) {
                renderer = new XmlXhtmlRenderer();
            }

            return renderer.highlight("test", text, "UTF8", true);
        } catch (IOException ex) {
            throw new HighlightingPluginException("Parsing was not successful\n" + ex);
        } catch (NullPointerException ex) {
            throw new HighlightingPluginException("Renderer was not successfully loaded for mime type "+mimeType);
        }
    }

    @Override
    public String getPurposes() {
        String result = MimeTypeNames.JAVA + " " + MimeTypeNames.CPP + " " + MimeTypeNames.XML + " " + MimeTypeNames.HTML;
        return result;
    }

    @Override
    public String getVersion() {
        return "0.1-SNAPSHOT";
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
