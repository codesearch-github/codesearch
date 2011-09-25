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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.indexer.server.exceptions;

/**
 *
 * @author Samuel Kogler
 */
public class JobExecutionException extends Exception {

    /** . */
    private static final long serialVersionUID = -985088845757068510L;


    /**
     * Creates a new instance of <code>JobExecutionException</code> without detail message.
     */
    public JobExecutionException() {
    }


    /**
     * Constructs an instance of <code>JobExecutionException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public JobExecutionException(String msg) {
        super(msg);
    }
}
