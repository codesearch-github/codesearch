
package org.codesearch.searcher.shared;

import java.util.List;

/**
 *
 * @author Samuel Kogler
 */
public interface SidebarNode {
    void onClick();
    String getDisplayText();
    List<SidebarNode> getChilds();
}
