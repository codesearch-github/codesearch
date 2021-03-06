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

package org.codesearch.searcher.shared.exception;

import java.io.Serializable;

/**
 * Thrown when an error in the searcher service server backend occurs.
 * @author Samuel Kogler
 */
public class SearcherServiceException extends Exception implements Serializable {


    /** . */
    private static final long serialVersionUID = 8931177812860079002L;

    /**
     * Creates a new instance of <code>SearcherServiceException</code> without detail message.
     */
    public SearcherServiceException() {
    }

    /**
     * Constructs an instance of <code>SearcherServiceException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public SearcherServiceException(String msg) {
        super(msg);
    }

    /**
     * @param message
     * @param cause
     */
    public SearcherServiceException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public SearcherServiceException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
    
}
