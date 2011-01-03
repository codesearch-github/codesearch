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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.configuration.ConfigurationException;
import org.codesearch.commons.configuration.xml.XmlConfigurationReader;
import org.codesearch.commons.configuration.xml.XmlConfigurationReaderConstants;

/**
 * DBUtils provides methods for access to the database specified in the configuration
 * @author David Froehlich
 */
public class DBAccess {
    private static XmlConfigurationReader configReader = new XmlConfigurationReader(); //TODO replace this with spring conform solution
    private static ConnectionPool connectionPool;
    private static final String STMT_GET_LAST_ANALYZED_REVISION_OF_REPOSITORY = "SELECT last_analyzed_revision FROM repository WHERE repository_name = ?";
    private static final String STMT_CREATE_REPOSITORY_ENTRY = "INSERT INTO repository (repository_name) VALUES (?)";
    private static final String STMT_SET_LAST_ANALYZED_REVISION_OF_REPOSITORY = "UPDATE repository SET last_analyzed_revision = ? WHERE repository_name = ?";
    private static final String STMT_SET_BINARY_INDEX_FOR_FILE = "UPDATE file SET binary_index=? WHERE file_path = ? AND repository_id = (SELECT repository_id FROM repository WHERE repository_name = ?)";
    private static final String STMT_GET_BINARY_INDEX_FOR_FILE = "SELECT binary_index FROM file where file_path = ? AND repository_id = (SELECT repository_id FROM repository WHERE repository_name = ?)";
    private static final String STMT_GET_FILE_RECORD = "SELECT * FROM file bi JOIN repository r ON bi.repository_id = r.repository_id WHERE bi.file_path = ? AND r.repository_name = ?";
    private static final String STMT_CREATE_FILE_RECORD = "INSERT INTO file (file_path, repository_id) VALUES (?, (SELECT repository_id from repository where repository_name = ?))";

    public static void setupConnections() throws ConfigurationException {
        connectionPool = ConnectionPool.getInstance();
        String dbUserName = configReader.getSingleLinePropertyValue(XmlConfigurationReaderConstants.DB_USERNAME);
        String dbPassword = configReader.getSingleLinePropertyValue(XmlConfigurationReaderConstants.DB_PASSWORD);
        String driver = configReader.getSingleLinePropertyValue(XmlConfigurationReaderConstants.DB_DRIVER);
        String url = configReader.getSingleLinePropertyValue(XmlConfigurationReaderConstants.DB_URL);
        String portNumber = configReader.getSingleLinePropertyValue(XmlConfigurationReaderConstants.DB_PORT_NUMBER);
        String dbName = configReader.getSingleLinePropertyValue(XmlConfigurationReaderConstants.DB_NAME);
        String dbms = configReader.getSingleLinePropertyValue(XmlConfigurationReaderConstants.DBMS);
        int dbMaxConnections = Integer.parseInt(configReader.getSingleLinePropertyValue(XmlConfigurationReaderConstants.DB_MAX_CONNECTIONS));
        connectionPool.setProperties(dbUserName, dbPassword, driver, url, portNumber, dbName, dbms, dbMaxConnections);
    }

    public static String getLastAnalyzedRevisionOfRepository(String repositoryName) throws DatabaseAccessException {
        try {
            Connection conn = connectionPool.getConnection();
            PreparedStatement ps = conn.prepareStatement(STMT_GET_LAST_ANALYZED_REVISION_OF_REPOSITORY);
            ps.setString(1, repositoryName);
            ResultSet rs = ps.executeQuery();
            connectionPool.releaseConnection(conn);
            if (!rs.first()) {
                createRepositoryEntry(repositoryName);
                return "0";
            } else {
                return rs.getString("last_analyzed_revision");
            }
        } catch (SQLException ex) {
            throw new DatabaseAccessException("Could not close the stream used to retrieve the content of the binary-index field\n" + ex);
        }
    }

    //TODO replace Object
    //the return type object is gonna be changed to a more fitting one as soon as it is clear which information is being stored in the database
    public static Object getBinaryIndexForFile(String filePath, String repository) throws DatabaseAccessException {
        {
            ObjectInputStream regObjectStream = null;
            try {
                Connection conn = connectionPool.getConnection();
                PreparedStatement ps = conn.prepareStatement(STMT_GET_BINARY_INDEX_FOR_FILE);
                ps.setString(1, filePath);
                ps.setString(2, repository);
                ResultSet rs = ps.executeQuery();
                rs.first();
                byte[] regBytes = rs.getBytes("binary_index");
                ByteArrayInputStream regArrayStream = new ByteArrayInputStream(regBytes);
                regObjectStream = new ObjectInputStream(regArrayStream);
                return regObjectStream.readObject();
            } catch (ClassNotFoundException ex) {
                throw new DatabaseAccessException("The content of the blob storing the binary index of file " + filePath + " repository " + repository + " could not be parsed to an Object, the database content is probably corrupt");
            } catch (IOException ex) {
                throw new DatabaseAccessException("Could not create a ByteArrayInputStream from the content of the binaryIndex field in the database");
            } catch (SQLException ex) {
                throw new DatabaseAccessException("SQLException while accessing the DB\n" + ex);
            } finally {
                try {
                    regObjectStream.close();
                } catch (IOException ex) {
                    throw new DatabaseAccessException("Could not close the stream used to retrieve the content of the binary-index field\n" + ex);
                }
            }
        }
    }

    public static void createRepositoryEntry(String repositoryName) throws DatabaseAccessException {
        try {
            Connection conn = connectionPool.getConnection();
            PreparedStatement ps = conn.prepareStatement(STMT_CREATE_REPOSITORY_ENTRY);
            ps.setString(1, repositoryName);
            ps.execute();
            connectionPool.releaseConnection(conn);
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        }
    }

    public static void setLastAnalyzedRevisionOfRepository(String repositoryName, String revision) throws DatabaseAccessException {
        try {
            Connection conn = connectionPool.getConnection();
            PreparedStatement ps = conn.prepareStatement(STMT_SET_LAST_ANALYZED_REVISION_OF_REPOSITORY);
            ps.setString(1, revision);
            ps.setString(2, repositoryName);
            ps.execute();
            connectionPool.releaseConnection(conn);
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        }
    }

    public static void setBinaryIndexForFile(String filePath, String repository, Object binaryIndex) throws DatabaseAccessException {
        try {
            ensureThatRecordExists(filePath, repository);
            Connection conn = connectionPool.getConnection();
            PreparedStatement ps = conn.prepareStatement(STMT_SET_BINARY_INDEX_FOR_FILE);
            ps.setObject(1, binaryIndex);
            ps.setString(2, filePath);
            ps.setString(3, repository);
            ps.executeUpdate();
            //ps.setObject(1, binaryIndex);
            //        ps.setObject(1, binaryIndex);
            //        ps.setString(2, filePath);
            //   //     ps.setString(3, repository);
            //ps.executeUpdate();
            connectionPool.releaseConnection(conn);
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        }
    }

    public static void ensureThatRecordExists(String filePath, String repository) throws DatabaseAccessException {
        try {
            Connection conn = connectionPool.getConnection();
            PreparedStatement ps = conn.prepareStatement(STMT_GET_FILE_RECORD);
            ps.setString(1, filePath);
            ps.setString(2, repository);
            ps.execute();
            if (!ps.getResultSet().first()) {
                //In case no record for this data exists
                ps = conn.prepareStatement(STMT_CREATE_FILE_RECORD);
                ps.setString(1, filePath);
                ps.setString(2, repository);
                ps.execute();
            }
            connectionPool.releaseConnection(conn);
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        }
    }
}
