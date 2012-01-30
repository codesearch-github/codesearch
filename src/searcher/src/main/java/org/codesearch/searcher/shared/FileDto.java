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

package org.codesearch.searcher.shared;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a file.
 * @author Samuel Kogler
 */
public class FileDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fileContent;
    private boolean binary;
    private List<OutlineNode> outline;
    private int focusLine;

    public List<OutlineNode> getOutline() {
        return outline;
    }

    public void setOutline(List<OutlineNode> outline) {
        this.outline = outline;
    }

    public boolean isBinary() {
        return binary;
    }

    public void setBinary(boolean binary) {
        this.binary = binary;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public int getFocusLine() {
        return focusLine;
    }

    public void setFocusLine(int focusLine) {
        this.focusLine = focusLine;
    }
}
