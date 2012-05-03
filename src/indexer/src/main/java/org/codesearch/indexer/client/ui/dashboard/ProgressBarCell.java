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
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;


public class ProgressBarCell extends NumberCell {

    class ProgressBarWidthSafeStyle implements SafeStyles {

        private static final long serialVersionUID = 8047291965851160143L;
        private final int value;

        public ProgressBarWidthSafeStyle(int value) {
            this.value = value;
        }

        @Override
        public String asString() {
            return "width:" + value + "px;";
        }

    }

    interface ProgressBarCellTemplates extends SafeHtmlTemplates {

        @Template(// @formatter:off
              "<div id='codeSearchProgressBarBackground' style='{1}border: 1px solid;'>"
            + "  <div id='codeSearchProgressBarValue' style='{2}background: lightblue;text-align: right;'>"
            + "&nbsp;{0}&nbsp;"
            + "  </div>"
            + "</div>")
        // @formatter:on
        SafeHtml progressBarWithValue(SafeHtml message, SafeStyles progressbarWidth, SafeStyles progressbarValue);
    }

    private static final ProgressBarCellTemplates TEMPLATES = GWT.create(ProgressBarCellTemplates.class);

    private int progressbarWidth = 300;

    public ProgressBarCell(int width) {
        progressbarWidth = width;
    }

    @Override
    public void render(Context context, Number value, SafeHtmlBuilder sb) {
        SafeHtml message = SafeHtmlUtils.fromString(String.valueOf((int)(value.floatValue() * 100) + "%"));
        ProgressBarWidthSafeStyle barWidth = new ProgressBarWidthSafeStyle(progressbarWidth);
        ProgressBarWidthSafeStyle valueWidth = new ProgressBarWidthSafeStyle(
            (int)(value.floatValue() * progressbarWidth));
        SafeHtml safeHtml = TEMPLATES.progressBarWithValue(message, barWidth, valueWidth);
        sb.append(safeHtml);
        GWT.log(safeHtml.asString());
    }
}
