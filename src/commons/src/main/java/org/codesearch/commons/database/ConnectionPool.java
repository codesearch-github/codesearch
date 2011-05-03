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


package org.codesearch.commons.database;

import java.sql.Connection;

/**
 * Provides access to database connections and manages them.
 * @author Samuel Kogler
 */
public interface ConnectionPool {

    /**
     * returns a connection to the database from the connectionPool
     * creates a new one in case no connections are available as long as the limit of connections is not exceeded
     * @return a connection
     * @throws DatabaseAccessException
     */
    Connection getConnection() throws DatabaseAccessException;

    /**
     * releases the connection back into the pool
     * @param conn the connection that is not used anymore
     */
    void releaseConnection(Connection conn);

}
