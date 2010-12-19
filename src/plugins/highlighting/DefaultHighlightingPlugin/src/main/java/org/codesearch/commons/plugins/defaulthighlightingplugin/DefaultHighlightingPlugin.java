package org.codesearch.commons.plugins.defaulthighlightingplugin;

import com.uwyn.jhighlight.renderer.CppXhtmlRenderer;
import com.uwyn.jhighlight.renderer.JavaXhtmlRenderer;
import com.uwyn.jhighlight.renderer.XhtmlRenderer;
import com.uwyn.jhighlight.renderer.XmlXhtmlRenderer;
import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import org.codesearch.commons.plugins.highlighting.HighlightingPlugin;
import org.codesearch.commons.plugins.highlighting.HighlightingPluginException;
import org.codesearch.commons.utils.MimeTypeUtil;
import org.springframework.stereotype.Component;

/**
 * A highlighting plugin used to highlight Java, CPP, XML, and HTML source code.
 * Can also escape image files(JPEG, PNG, BMP, TIFF) to HTML.
 * @author David Froehlich
 */
@Component
public class DefaultHighlightingPlugin implements HighlightingPlugin {

    XhtmlRenderer renderer;

    /** {@inheritDoc} */
    @Override
    public String parseToHtml(byte[] content, String mimeType) throws HighlightingPluginException {
        try {
            if (mimeType.equals(MimeTypeUtil.JAVA)) {
                renderer = new JavaXhtmlRenderer();
            } else if (mimeType.equals(MimeTypeUtil.CPP)) {
                renderer = new CppXhtmlRenderer();
            } else if (mimeType.equals(MimeTypeUtil.HTML)) {
                renderer = new XmlXhtmlRenderer();
            } else if (mimeType.equals(MimeTypeUtil.XML)) {
                renderer = new XmlXhtmlRenderer();
            } else if (mimeType.equals(MimeTypeUtil.PNG)  ||
                       mimeType.equals(MimeTypeUtil.JPEG) ||
                       mimeType.equals(MimeTypeUtil.BMP)  ||
                       mimeType.equals(MimeTypeUtil.TIFF)) {
                return escapeImage(content, mimeType);
            } else {
                renderer = new XmlXhtmlRenderer();
            }
            return renderer.highlight(null, new String(content), "UTF-8", true);
        } catch (IOException ex) {
            throw new HighlightingPluginException("Parsing was not successful\n" + ex);
        } catch (NullPointerException ex) {
            throw new HighlightingPluginException("Renderer was not successfully loaded for mime type " + mimeType);
        }
    }

    /**
     * Escapes an image to HTML with the binary content of the file as source
     * @param content the file content
     * @param mime the mime type of the image
     * @return the image as HTML code
     */
    private String escapeImage(byte[] content, String mime) {
        String base64 = Base64.encodeBase64String(content);
        return "<img name='image' alt='image' src='data:" + mime + ";base64," + base64 + "' />";
    }

    /** {@inheritDoc} */
    @Override
    public String getPurposes() {
        String result = MimeTypeUtil.JAVA + " "
                      + MimeTypeUtil.CPP + " "
                      + MimeTypeUtil.XML + " "
                      + MimeTypeUtil.HTML + " "
                      + MimeTypeUtil.PNG + " "
                      + MimeTypeUtil.JPEG + " "
                      + MimeTypeUtil.TIFF + " "
                      + MimeTypeUtil.BMP;
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
