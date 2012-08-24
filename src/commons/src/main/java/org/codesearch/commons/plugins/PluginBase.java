package org.codesearch.commons.plugins;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks an interface as a plugin base which makes it possible to separate plugin
 * interfaces from their implementations while loading plugins.
 */
@Retention(RetentionPolicy.RUNTIME)  
public @interface PluginBase {

}
