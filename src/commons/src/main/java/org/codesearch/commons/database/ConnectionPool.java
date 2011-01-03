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
package org.codesearch.commons.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David Froehlich
 */
public class ConnectionPool {

    private static ConnectionPool theInstance = null;
    private LinkedList<Connection> connections = new LinkedList<Connection>();
    private int remainingConnections = 150;
    private String username;
    private String password;
    private String driver;
    private String url;
    private String portNumber;
    private String dbName;
    private String dbms;

    public void setProperties(String username, String password, String driver, String url, String portNumber, String dbName, String dbms, int remainingConneciton) {
        this.username = username;
        this.password = password;
        this.driver = driver;
        this.url = url;
        this.portNumber = portNumber;
        this.dbName = dbName;
        this.remainingConnections = remainingConneciton;
        this.dbms = dbms;
    }

    private ConnectionPool() {
    }

    public static ConnectionPool getInstance() {
        if (theInstance == null) {
            theInstance = new ConnectionPool();
        }
        return theInstance;
    }

    public Connection getConnection() throws DatabaseAccessException {
        if (remainingConnections > 0) {
            remainingConnections--;
            if (connections.isEmpty()) {
                try {
                    Class.forName(driver);
                    Connection conn = DriverManager.getConnection("jdbc:" + dbms + "://" + url + ":" + portNumber + "/" + dbName, "root", "");
                    return conn;
                } catch (SQLException ex) {
                    throw new DatabaseAccessException("SQLException while trying to poll new connection\n"+ex);
                } catch (ClassNotFoundException ex) {
                    throw new DatabaseAccessException("Specified driver could not be found\n"+ex);
                }
            } else {
                return connections.pollFirst();
            }
        } else {
            throw new DatabaseAccessException("All connections currently in use, increase connection limit in configuration and check if a connection is not released in DBUtils");
        }
    }

    public void releaseConnection(Connection conn) {
        connections.offerLast(conn);
        remainingConnections++;
    }
}
