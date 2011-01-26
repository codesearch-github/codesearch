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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

import org.apache.log4j.Logger;

/**
 *
 * @author David Froehlich
 */
public class ConnectionPool {

    private static ConnectionPool theInstance = null;
    private static Logger LOG = Logger.getLogger(ConnectionPool.class);
    private LinkedList<Connection> connections = new LinkedList<Connection>();
    private int remainingConnections = 150;
    private String username;
    private String password;
    private String driver;
    private String url;
    private String portNumber;
    private String dbName;
    private String dbms;

    /**
     * sets the properties of the ConnectionPool
     * @param username the username used for the database access
     * @param password the password used for the database access
     * @param driver the driver used for the database access
     * @param url the url of the database
     * @param portNumber the port used to access the database
     * @param dbName the name of the database
     * @param dbms the  //FIXMW
     * @param remainingConnections the maximum amount of connections to the database
     */
    public void setProperties(String username, String password, String driver, String url, String portNumber, String dbName, String dbms, int remainingConnections) {
        this.username = username;
        this.password = password;
        this.driver = driver;
        this.url = url;
        this.portNumber = portNumber;
        this.dbName = dbName;
        this.remainingConnections = remainingConnections;
        this.dbms = dbms;
    }

    private ConnectionPool() {
    }

    /**
     * returns the singleton instance of the ConnectionPool
     * @return the instance
     */
    public static synchronized ConnectionPool getInstance() {
        if (theInstance == null) {
            theInstance = new ConnectionPool();
        }
        return theInstance;
    }

    /**
     * returns a connection to the database from the connectionPool
     * creates a new one in case no connections are available as long as the limit of connections is not exceeded
     * @return a connection
     * @throws DatabaseAccessException
     */
    public Connection getConnection() throws DatabaseAccessException {
        if (remainingConnections > 0) {
            remainingConnections--;
            if (connections.isEmpty()) {
                try {
                    Class.forName(driver);
                    String connStr = "jdbc:" + dbms + "://" + url + ":" + portNumber + "/" + dbName;
                    LOG.debug("Connecting to database: " + connStr);

                    Connection conn = DriverManager.getConnection("jdbc:" + dbms + "://" + url + ":" + portNumber + "/" + dbName, username, password);
                    return conn;
                } catch (SQLException ex) {
                    throw new DatabaseAccessException("SQLException while trying to poll new connection\n" + ex);
                } catch (ClassNotFoundException ex) {
                    throw new DatabaseAccessException("Specified driver could not be found\n" + ex);
                }
            } else {
                return connections.pollFirst();
            }
        } else {
            throw new DatabaseAccessException("All connections currently in use, increase connection limit in configuration and check if a connection is not released in DBAccess");
        }
    }

    /**
     * releases the connection back into the pool
     * @param conn the connection that is not used anymore
     */
    public void releaseConnection(Connection conn) {
        connections.offerLast(conn);
        remainingConnections++;
    }
}
