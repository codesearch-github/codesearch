
package org.codesearch.commons.plugins;

/**
 *
 * @author David Froehlich
 */
public interface Plugin {
    /**
     * Returns the purpose of this plugin,
     * Can be used to distinguish between multiple plugins that have the same base-functionality but are used in different situations
     * for instance getPurpose of a VersionControlPlugin could either return 'SVN' or 'Bazaar' and so forth
     * @return the purpose of the plugin
     */
    String getPurpose();

    /**
     * Returns the version of the plugin
     * @return the version of the plugin
     */
    String getVersion();   
}
