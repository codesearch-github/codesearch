package org.codesearch.searcher.shared;

import java.io.Serializable;
import java.util.List;

import org.codesearch.searcher.client.ui.fileview.FileViewImpl;

/**
 *
 * @author Samuel Kogler
 */
public class OutlineNode implements SidebarNode, Serializable {
    private List<SidebarNode> childs;
    private String name;
    private int startLine;
    private String cssClasses;

    public String getCssClasses() {
        return cssClasses;
    }

    public void setCssClasses(String cssClasses) {
        this.cssClasses = cssClasses;
    }
    
    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    @Override
    public List<SidebarNode> getChilds() {
        return childs;
    }

    public void setChilds(List<SidebarNode> childs) {
        this.childs = childs;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void onClick() {
        FileViewImpl.staticGoToLine(startLine);
    }

    @Override
    public String getDisplayText() {
        return name;
    }

}
