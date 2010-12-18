/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.defaulthighlightingplugin;

import com.uwyn.jhighlight.renderer.CppXhtmlRenderer;
import com.uwyn.jhighlight.renderer.JavaXhtmlRenderer;
import com.uwyn.jhighlight.renderer.XhtmlRenderer;
import com.uwyn.jhighlight.renderer.XhtmlRendererFactory;
import com.uwyn.jhighlight.renderer.XmlXhtmlRenderer;
import java.io.IOException;
import org.codesearch.commons.plugins.highlighting.HighlightingPlugin;
import org.codesearch.commons.plugins.highlighting.HighlightingPluginException;
import org.codesearch.commons.utils.MimeTypeUtil;
import org.springframework.stereotype.Component;

/**
 * A highlighting plugin used to highlight java, cpp, xml, and html files
 * @author David Froehlich
 */
@Component
public class DefaultHighlightingPlugin implements HighlightingPlugin {

    XhtmlRenderer renderer;

    /** {@inheritDoc} */
    @Override
    public String parseToHtml(String text, String mimeType) throws HighlightingPluginException {
        try {
            if (mimeType.equals(MimeTypeUtil.JAVA)) {
                renderer = new JavaXhtmlRenderer();
            } else if (mimeType.equals(MimeTypeUtil.CPP)) {
                renderer = new CppXhtmlRenderer();
            } else if (mimeType.equals(MimeTypeUtil.HTML)) {
                //       renderer = new XhtmlRenderer() {}
                renderer = new XmlXhtmlRenderer();
            } else if (mimeType.equals(MimeTypeUtil.XML)) {
                renderer = new XmlXhtmlRenderer();
            } else if (mimeType.equals(MimeTypeUtil.PNG)) {
                return this.parseBinaryContentToImg(text);
            } else {
                renderer = new XmlXhtmlRenderer();
            }
            //TODO add file name
            return renderer.highlight("test", text, "UTF8", true);
        } catch (IOException ex) {
            throw new HighlightingPluginException("Parsing was not successful\n" + ex);
        } catch (NullPointerException ex) {
            throw new HighlightingPluginException("Renderer was not successfully loaded for mime type " + mimeType);
        }
    }

    /**
     * creates an html image with the binary content of the file as source
     * @param content the file content
     * @return the image as a <img> tag
     */
    private String parseBinaryContentToImg(String content) {
        String img = "<img name='image' alt='image of searched file' src=";
        img += content + "/>";
        return img;
    }

    /** {@inheritDoc} */
    @Override
    public String getPurposes() {
        String result = MimeTypeUtil.JAVA + " " + MimeTypeUtil.CPP + " " + MimeTypeUtil.XML + " " + MimeTypeUtil.HTML + " " + MimeTypeUtil.PNG;
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public String getVersion() {
        return "0.1-SNAPSHOT";
    }

    /** {@inheritDoc} */
    @Override
    public String getEscapeStartToken() {
        return "begin_escape";
    }

    /** {@inheritDoc} */
    @Override
    public String getEscapeEndToken() {
        return "end_escape";
    }
}
