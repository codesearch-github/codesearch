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

import com.mysql.jdbc.Statement;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.codesearch.commons.configuration.xml.XmlConfigurationReader;
import org.codesearch.commons.configuration.xml.XmlConfigurationReaderConstants;
import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.Usage;

/**
 * DBUtils provides methods for access to the database specified in the configuration
 * @author David Froehlich
 */
public class DBAccess {

    private static XmlConfigurationReader configReader = new XmlConfigurationReader(); //TODO replace this with spring conform solution
    private static ConnectionPool connectionPool;
    private static final Logger LOG = Logger.getLogger(DBAccess.class);
    private static final String STMT_GET_LAST_ANALYZED_REVISION_OF_REPOSITORY = "SELECT last_analyzed_revision FROM repository WHERE repository_name = ?";
    private static final String STMT_CREATE_REPOSITORY_ENTRY = "INSERT INTO repository (repository_name) VALUES (?)";
    private static final String STMT_SET_LAST_ANALYZED_REVISION_OF_REPOSITORY = "UPDATE repository SET last_analyzed_revision = ? WHERE repository_name = ?";
    private static final String STMT_SET_BINARY_INDEX_FOR_FILE = "UPDATE file SET binary_index = ?, usages = ? WHERE file_id = ?";
    private static final String STMT_CLEAR_TYPES_FOR_FILE = "DELETE FROM type WHERE file_id = ?";
    //private static final String STMT_SET_TYPES_FOR_FILE = "INSERT INTO type (full_name, file_id, repo_id) VALUES (?, (SELECT file_id FROM file WHERE file_path = ? AND repository = (SELECT repository_id FROM repository WHERE repository_name = ?)), (SELECT repository_id FROM repository WHERE repository_name = ?))";
    private static final String STMT_GET_BINARY_INDEX_FOR_FILE = "SELECT binary_index FROM file where file_path = ? AND repository_id = (SELECT repository_id FROM repository WHERE repository_name = ?)";
    private static final String STMT_GET_FILE_RECORD = "SELECT file_id FROM file bi JOIN repository r ON bi.repository_id = r.repository_id WHERE bi.file_path = ? AND r.repository_name = ?";
    private static final String STMT_CREATE_FILE_RECORD = "INSERT INTO file (file_path, repository_id) VALUES (?, (SELECT repository_id from repository where repository_name = ?))";
    private static final String STMT_GET_REPO_ID_FOR_NAME = "SELECT repository_id FROM repository where repository_name = ?";
    private static final String STMT_CREATE_TYPES_FOR_FILE = "INSERT INTO type (full_name, file_id, repo_id) VALUES ";
    private static final String STMT_GET_FILE_PATH_FOR_TYPE_DECLARATION = "SELECT f.file_path FROM file f JOIN type t ON f.file_id = t.file_id WHERE f.repository_id = ? AND t.full_name LIKE ?";
    private static final String STMT_GET_FILE_PATH_FOR_TYPE_DECLARATION_WITH_PACKAGES = "SELECT f.file_path FROM file f JOIN type t ON f.file_id = t.file_id WHERE f.repository_id = ? AND t.full_name IN (";
    private static final String STMT_GET_USAGES_FOR_FILE = "SELECT usages FROM file WHERE file_path = ? AND repository_id = ?";

    public static List<Usage> getUsagesForFile(String filePath, String repository) throws DatabaseAccessException {
        ObjectInputStream regObjectStream = null;
        try {
            if (connectionPool == null) {
                setupConnections();
            }
            Connection conn = connectionPool.getConnection();
            int repo_id = getRepoIdForRepoName(repository);
            PreparedStatement ps = conn.prepareStatement(STMT_GET_USAGES_FOR_FILE);
            ps.setString(1, filePath);
            ps.setInt(2, repo_id);
            ResultSet rs = ps.executeQuery();
            if(rs.first()){
                byte[] regBytes = rs.getBytes("usages");
            ByteArrayInputStream regArrayStream = new ByteArrayInputStream(regBytes);
            regObjectStream = new ObjectInputStream(regArrayStream);
            return (List<Usage>) regObjectStream.readObject();
                
            }
            return null;
        } catch (IOException ex) {
            throw new DatabaseAccessException("The content of the blob storing the usages of file " + filePath + " repository " + repository + " could not be parsed to an Object, the database content is probably corrupt");
        } catch (ClassNotFoundException ex) {
            throw new DatabaseAccessException("The content of the blob storing the usages of file " + filePath + " repository " + repository + " could not be parsed to an Object, the database content is probably corrupt");
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        }
    }

    public static String getFilePathForTypeDeclaration(String fullyQualifiedName, String repository) throws DatabaseAccessException {
        try {
            if (connectionPool == null) {
                setupConnections();
            }
            Connection conn = connectionPool.getConnection();
            int repo_id = getRepoIdForRepoName(repository);
            PreparedStatement ps = conn.prepareStatement(STMT_GET_FILE_PATH_FOR_TYPE_DECLARATION);
            ps.setInt(1, repo_id);
            ps.setString(2, fullyQualifiedName);
            ResultSet rs = ps.executeQuery();
            rs = ps.getResultSet();
            if (rs.first()) {
                return rs.getString("file_path");
            }
            return null;
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        }
    }

    public static String getFilePathForTypeDeclaration(String fullyQualifiedName, String repository, List<String> asteriskImports) throws DatabaseAccessException {
        try {
            if (connectionPool == null) {
                setupConnections();
            }
            Connection conn = connectionPool.getConnection();
            int repo_id = getRepoIdForRepoName(repository);
            String importString = "";
            for (String currentImport : asteriskImports) {
                importString += currentImport + "." + fullyQualifiedName + ", ";
            }
            importString = importString.substring(0, importString.length() - 2) + ")";
            PreparedStatement ps = conn.prepareStatement(STMT_GET_FILE_PATH_FOR_TYPE_DECLARATION_WITH_PACKAGES + importString);
            ps.setInt(1, repo_id);
            ResultSet rs = ps.executeQuery();
            rs.first();
            return rs.getString("file_path");
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        }
    }

    public static void setupConnections() {
        try {
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
        } catch (ConfigurationException ex) {
            LOG.error("Error at database configuration in config file\n" + ex);
        }
    }

    public static String getLastAnalyzedRevisionOfRepository(String repositoryName) throws DatabaseAccessException {
        try {
            if (connectionPool == null) {
                setupConnections();
            }
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
    public static List<AstNode> getBinaryIndexForFile(String filePath, String repository) throws DatabaseAccessException {
        if (connectionPool == null) {
            setupConnections();
        }
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
            return (List<AstNode>) regObjectStream.readObject();
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

    public static void createRepositoryEntry(String repositoryName) throws DatabaseAccessException {
        if (connectionPool == null) {
            setupConnections();
        }
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
        if (connectionPool == null) {
            setupConnections();
        }
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

    private static int getRepoIdForRepoName(String repoName) throws DatabaseAccessException {
        if (connectionPool == null) {
            setupConnections();
        }
        try {
            Connection conn = connectionPool.getConnection();
            PreparedStatement ps = conn.prepareStatement(STMT_GET_REPO_ID_FOR_NAME);
            ps.setString(1, repoName);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            rs.first();
            connectionPool.releaseConnection(conn);
            return rs.getInt("repository_id");
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        }
    }

    public static void setAnalysisDataForFile(String filePath, String repository, Object binaryIndex, Object usages, List<String> types) throws DatabaseAccessException {
        if (connectionPool == null) {
            setupConnections();
        }
        try {
            int fileId = ensureThatRecordExists(filePath, repository);
            int repoId = getRepoIdForRepoName(repository);
            Connection conn = connectionPool.getConnection();
            PreparedStatement ps = conn.prepareStatement(STMT_CLEAR_TYPES_FOR_FILE);
            ps.setInt(1, fileId);
            ps.execute();
            ps = conn.prepareStatement(STMT_SET_BINARY_INDEX_FOR_FILE);
            ps.setObject(1, binaryIndex);
            ps.setObject(2, usages);
            ps.setInt(3, fileId);
            ps.executeUpdate();
            if (!(types.isEmpty())) {
                //extends the insert into statement by one line for each type in the list of types
                String statementEnding = "";
                for (int i = 0; i < types.size(); i++) {
                    statementEnding += "(?, " + fileId + ", " + repoId + "),";
                }
                ps = conn.prepareStatement(STMT_CREATE_TYPES_FOR_FILE + statementEnding.substring(0, statementEnding.length() - 1));
                for (int i = 0; i < types.size(); i++) {
                    ps.setString(i + 1, types.get(i));
                }
            }
            ps.execute(); //FIXME
            connectionPool.releaseConnection(conn);
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        }
    }

    public static int ensureThatRecordExists(String filePath, String repository) throws DatabaseAccessException {
        if (connectionPool == null) {
            setupConnections();
        }
        try {
            ResultSet rs = null;
            Connection conn = connectionPool.getConnection();
            PreparedStatement ps = conn.prepareStatement(STMT_GET_FILE_RECORD, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, filePath);
            ps.setString(2, repository);
            ps.execute();
            rs = ps.getResultSet();
            if (!rs.first()) {
                //In case no record for this data exists
                ps = conn.prepareStatement(STMT_CREATE_FILE_RECORD, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, filePath);
                ps.setString(2, repository);
                ps.execute();
                ps.getGeneratedKeys().first();
                return rs.getInt(1);
            }
            connectionPool.releaseConnection(conn);
            return rs.getInt("file_id");
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        }
    }
}
