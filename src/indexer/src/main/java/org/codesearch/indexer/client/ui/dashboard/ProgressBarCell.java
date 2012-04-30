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

package org.codesearch.indexer.client.ui.dashboard;

import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;


public class ProgressBarCell extends NumberCell {

    private int progressbarWidth = 300;

    public ProgressBarCell(int width) {
        progressbarWidth = width;
    }

    @Override
    public void render(Context context, Number value, SafeHtmlBuilder sb) {
        sb.appendHtmlConstant("<div id='codeSearchProgressBarBackground' style='width:" + progressbarWidth
            + "px;border: 1px solid;'>");
        sb.appendHtmlConstant("<div id='codeSearchProgressBarValue' style='width:"
            + (int)(value.floatValue() * progressbarWidth) + "px;background: lightblue;'>&nbsp;");
        sb.append((int)(value.floatValue() * 100));
        sb.appendEscaped("%");
        sb.appendHtmlConstant("</div>");
        sb.appendHtmlConstant("</div>");
        GWT.log(sb.toSafeHtml().asString());
    }
}
