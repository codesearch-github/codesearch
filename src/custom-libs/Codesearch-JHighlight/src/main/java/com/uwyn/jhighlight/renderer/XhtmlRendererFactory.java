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

package com.uwyn.jhighlight.renderer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Provides a single point of entry to instantiate Xhtml renderers.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3108 $
 * @since 1.0
 */
public abstract class XhtmlRendererFactory {

    public final static String GROOVY = "groovy";
    public final static String JAVA = "java";
    public final static String BEANSHELL = "beanshell";
    public final static String BSH = "bsh";
    public final static String XML = "xml";
    public final static String XHTML = "xhtml";
    public final static String LZX = "lzx";
    public final static String HTML = "html";
    public final static String CPP = "cpp";
    public final static String CXX = "cxx";
    public final static String CPLUSPLUS = "c++";
    private final static Map RENDERERS_CLASSNAMES = new HashMap() {

        {
            put(GROOVY, GroovyXhtmlRenderer.class.getName());
            put(JAVA, JavaXhtmlRenderer.class.getName());
            put(BEANSHELL, JavaXhtmlRenderer.class.getName());
            put(BSH, JavaXhtmlRenderer.class.getName());
            put(XML, XmlXhtmlRenderer.class.getName());
            put(XHTML, XmlXhtmlRenderer.class.getName());
            put(LZX, XmlXhtmlRenderer.class.getName());
            put(HTML, XmlXhtmlRenderer.class.getName());
            put(CPP, CppXhtmlRenderer.class.getName());
            put(CXX, CppXhtmlRenderer.class.getName());
            put(CPLUSPLUS, CppXhtmlRenderer.class.getName());
        }
    };

    /**
     * Instantiates an instance of a known <code>XhtmlRenderer</code> according to
     * the type that's provided.
     *
     * @param type The type of renderer, look at the static variables of this
     * class to see which ones are supported.
     * @return an instance of the <code>XhtmlRenderer</code> that corresponds to the type; or
     * <p><code>null</code> if the type wasn't known
     * @since 1.0
     */
    public static Renderer getRenderer(String type) {
        String classname = (String) RENDERERS_CLASSNAMES.get(type.toLowerCase());
        if (null == classname) {
            return null;
        }

        try {
            Class klass = Class.forName(classname);
            return (Renderer) klass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returned a set with all the supported XHTML renderer types.
     *
     * @return a <code>Set</code> with the supported XHTML renderer types as strings.
     * @since 1.0
     */
    public static Set getSupportedTypes() {
        return Collections.unmodifiableSet(RENDERERS_CLASSNAMES.keySet());
    }
}
