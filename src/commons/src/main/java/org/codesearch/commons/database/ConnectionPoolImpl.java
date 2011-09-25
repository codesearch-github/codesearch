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
import org.codesearch.commons.configuration.ConfigurationReader;
import org.codesearch.commons.configuration.dto.DatabaseConfiguration;

import com.google.inject.Inject;

/**
 *
 * @author David Froehlich
 */
public class ConnectionPoolImpl implements ConnectionPool {

    private static Logger LOG = Logger.getLogger(ConnectionPoolImpl.class);
    private LinkedList<Connection> connections = new LinkedList<Connection>();

    private int remainingConnections = 150;
    private DatabaseConfiguration configuration;


    @Inject
    public ConnectionPoolImpl(ConfigurationReader configurationReader) {
        configuration = configurationReader.getDatabaseConfiguration();
        remainingConnections = configuration.getMaxConnections();
    }

    /** {@inheritDoc} */
    @Override
    public synchronized Connection getConnection() throws DatabaseAccessException {
        if (remainingConnections > 0) {
            remainingConnections--;
            if (connections.isEmpty()) {
                try {
                    Class.forName(configuration.getDriver());
                    String connStr = "jdbc:" + configuration.getProtocol() + "://" + configuration.getHostName() + ":" + configuration.getPort() + "/" + configuration.getDatabase();
                    LOG.debug("Connecting to database: " + connStr);

                    Connection conn = DriverManager.getConnection(connStr, configuration.getUsername(), configuration.getPassword());
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

    /** {@inheritDoc} */
    @Override
    public synchronized void releaseConnection(Connection conn) {
        connections.offerLast(conn);
        remainingConnections++;
    }
}
