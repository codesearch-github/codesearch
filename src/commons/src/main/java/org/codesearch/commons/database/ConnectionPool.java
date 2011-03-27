
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
