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

import com.mysql.jdbc.Statement;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    private static final String STMT_SET_BINARY_INDEX_AND_USAGES_FOR_FILE = "UPDATE file SET binary_index = ?, usages = ? WHERE file_id = ?";
    private static final String STMT_SET_IMPORTS_FOR_FILE = "INSERT INTO import (source_file_id, target_file_path) VALUES ";
    private static final String STMT_CLEAR_IMPORTS_FOR_FILE = "DELETE FROM import where source_file_id = ?";
    private static final String STMT_CLEAR_TYPES_FOR_FILE = "DELETE FROM type WHERE file_id = ?";
    private static final String STMT_GET_FILES_IMPORTING_FILE = "SELECT f.file_path, f.binary_index from file f JOIN import i ON f.file_id = i.source_file_id WHERE i.target_file_path IN (?, ?)";
    private static final String STMT_GET_BINARY_INDEX_FOR_FILE = "SELECT binary_index FROM file where file_path = ? AND repository_id = (SELECT repository_id FROM repository WHERE repository_name = ?)";
    private static final String STMT_GET_FILE_ID_FOR_FILE_NAME = "SELECT file_id FROM file WHERE file_path = ? AND repository_id = ?";
    private static final String STMT_CREATE_FILE_RECORD = "INSERT INTO file (file_path, repository_id) VALUES (?, (SELECT repository_id from repository where repository_name = ?))";
    private static final String STMT_GET_REPO_ID_FOR_NAME = "SELECT repository_id FROM repository where repository_name = ?";
    private static final String STMT_CREATE_TYPES_FOR_FILE = "INSERT INTO type (full_name, file_id, repo_id) VALUES ";
    private static final String STMT_GET_FILE_PATH_FOR_TYPE_DECLARATION = "SELECT f.file_path FROM file f JOIN type t ON f.file_id = t.file_id WHERE f.repository_id = ? AND t.full_name LIKE ?";
    private static final String STMT_GET_FILE_PATH_FOR_TYPE_DECLARATION_WITH_PACKAGES = "SELECT f.file_path FROM file f JOIN type t ON f.file_id = t.file_id WHERE f.repository_id = ? AND t.full_name IN (";
    private static final String STMT_GET_USAGES_FOR_FILE = "SELECT usages FROM file WHERE file_path = ? AND repository_id = ?";
    private static final String STMT_CLEAR_TYPES_FOR_REPOSITORY = "DELETE FROM type WHERE repo_id = ?";
    private static final String STMT_CLEAR_FILES_FOR_REPOSITORY = "DELETE FROM file WHERE repository_id = ?";
    private static final String STMT_GET_IMPORTS_FOR_FILE = "SELECT imports FROM file WHERE file_path = ? AND repository_id = ?";

    private static void clearImportsForFile(int fileId) throws DatabaseAccessException {
        if (connectionPool == null) {
            setupConnections();
        }
        Connection conn = connectionPool.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(STMT_CLEAR_IMPORTS_FOR_FILE);
            ps.setInt(1, fileId);
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        } finally {
            connectionPool.releaseConnection(conn);
        }
    }

    /**
     * retrieves the imports as strings from the database
     * @param filePath the file path + name of the file
     * @param repository the name of the repository holding the file
     * @return the imports as a list of strings
     * @throws DatabaseAccessException
     */
    public static List<String> getImportsForFile(String filePath, String repository) throws DatabaseAccessException {
        if (connectionPool == null) {
            setupConnections();
        }
        List<String> imports = null;
        Connection conn = connectionPool.getConnection();
        try {
            ObjectInputStream regObjectStream = null;

            int repo_id = getRepoIdForRepoName(repository);
            PreparedStatement ps = conn.prepareStatement(STMT_GET_IMPORTS_FOR_FILE);
            ps.setString(1, filePath);
            ps.setInt(2, repo_id);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                byte[] regBytes = rs.getBytes("imports");
                if (regBytes != null) {
                    ByteArrayInputStream regArrayStream = new ByteArrayInputStream(regBytes);
                    regObjectStream = new ObjectInputStream(regArrayStream);
                    imports = (List<String>) regObjectStream.readObject();
                }
            }
        } catch (IOException ex) {
            throw new DatabaseAccessException("The content of the blob storing the imports of file " + filePath + " repository " + repository + " could not be parsed to an Object, the database content is probably corrupt");
        } catch (ClassNotFoundException ex) {
            throw new DatabaseAccessException("The content of the blob storing the imports of file " + filePath + " repository " + repository + " could not be parsed to an Object, the database content is probably corrupt");
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        } finally {
            connectionPool.releaseConnection(conn);
        }
        return imports;
    }

    /**
     * retrieves the usages in the file from the database
     * @param filePath the file path + name of the file
     * @param repository the name of the repository holding the file
     * @return the usages ordered by line number / column
     * @throws DatabaseAccessException
     */
    public static List<Usage> getUsagesForFile(String filePath, String repository) throws DatabaseAccessException {
        if (connectionPool == null) {
            setupConnections();
        }
        List<Usage> usages = null;
        Connection conn = connectionPool.getConnection();
        try {
            ObjectInputStream regObjectStream = null;

            int repo_id = getRepoIdForRepoName(repository);
            PreparedStatement ps = conn.prepareStatement(STMT_GET_USAGES_FOR_FILE);
            ps.setString(1, filePath);
            ps.setInt(2, repo_id);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                byte[] regBytes = rs.getBytes("usages");
                if (regBytes != null) {
                    ByteArrayInputStream regArrayStream = new ByteArrayInputStream(regBytes);
                    regObjectStream = new ObjectInputStream(regArrayStream);
                    usages = (List<Usage>) regObjectStream.readObject();
                }
            }
        } catch (IOException ex) {
            throw new DatabaseAccessException("The content of the blob storing the usages of the file " + filePath + " repository " + repository + " could not be parsed to an Object, the database content is probably corrupt");
        } catch (ClassNotFoundException ex) {
            throw new DatabaseAccessException("The content of the blob storing the usages of the file " + filePath + " repository " + repository + " could not be parsed to an Object, the database content is probably corrupt");
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        } finally {
            connectionPool.releaseConnection(conn);
        }
        return usages;
    }

    /**
     * retrieves the path of the file in which the from the database
     * @param filePath the file path + name of the file
     * @param repository the name of the repository holding the file
     * @return the imports as a list of strings
     * @throws DatabaseAccessException
     */
    public static String getFilePathForTypeDeclaration(String fullyQualifiedName, String repository) throws DatabaseAccessException {
        if (connectionPool == null) {
            setupConnections();
        }

        Connection conn = connectionPool.getConnection();
        String filePath = null;
        try {
            int repo_id = getRepoIdForRepoName(repository);
            PreparedStatement ps = conn.prepareStatement(STMT_GET_FILE_PATH_FOR_TYPE_DECLARATION);
            ps.setInt(1, repo_id);
            ps.setString(2, fullyQualifiedName);
            ResultSet rs = ps.executeQuery();
            rs = ps.getResultSet();
            if (rs.first()) {
                filePath = rs.getString("file_path");
            }
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        } finally {
            connectionPool.releaseConnection(conn);
        }
        return filePath;
    }

    /**
     * deletes all entries from the file and type table that are associated to the repository
     * @param repoName the name of the repository
     * @throws DatabaseAccessException
     */
    public static void clearTablesForRepository(String repoName) throws DatabaseAccessException {
        if (connectionPool == null) {
            setupConnections();
        }
        Connection conn = connectionPool.getConnection();
        try {
            int repoId = getRepoIdForRepoName(repoName);
            PreparedStatement ps = conn.prepareStatement(STMT_CLEAR_FILES_FOR_REPOSITORY);
            ps.setInt(1, repoId);
            ps.execute();
            ps = conn.prepareStatement(STMT_CLEAR_TYPES_FOR_REPOSITORY);
            ps.setInt(1, repoId);
            ps.execute();
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        } finally {
            connectionPool.releaseConnection(conn);
        }
    }

    /**
     * retrieves the file path of the file that contains the class
     * tries with all possible packages
     * @param className the name of class
     * @param repository the repository that holds the file
     * @param asteriskImports the packages that could contain the fiel
     * @return the path of the file
     * @throws DatabaseAccessException
     */
    public static String getFilePathForTypeDeclaration(String className, String repository, List<String> asteriskImports) throws DatabaseAccessException {
        if (asteriskImports.isEmpty()) {
            return null;
        }
        if (connectionPool == null) {
            setupConnections();
        }
        String filePath = null;
        Connection conn = connectionPool.getConnection();
        try {
            int repo_id = getRepoIdForRepoName(repository);
            String importString = "";
            for (String currentImport : asteriskImports) {
                importString += "'" + currentImport.substring(0, currentImport.length() - 1) + className + "', ";
            }
            importString = importString.substring(0, importString.length() - 2) + ")";
            PreparedStatement ps = conn.prepareStatement(STMT_GET_FILE_PATH_FOR_TYPE_DECLARATION_WITH_PACKAGES + importString);
            ps.setInt(1, repo_id);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                filePath = rs.getString("file_path");
            }
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        } finally {
            connectionPool.releaseConnection(conn);
        }
        return filePath;
    }

    /**
     * reads the values for the database connection from the configuration file and creates a connection to the database
     */
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

    /**
     * returns the revision number of the repository that was valid during the last code analysis process
     * @param repositoryName the repository
     * @return the revision 
     * @throws DatabaseAccessException
     */
    public static String getLastAnalyzedRevisionOfRepository(String repositoryName) throws DatabaseAccessException {
        if (connectionPool == null) {
            setupConnections();
        }
        String lastAnalyzedRevision = null;
        Connection conn = connectionPool.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(STMT_GET_LAST_ANALYZED_REVISION_OF_REPOSITORY);
            ps.setString(1, repositoryName);
            ResultSet rs = ps.executeQuery();
            if (!rs.first()) {
                createRepositoryEntry(repositoryName);
                lastAnalyzedRevision = "0";
            } else {
                lastAnalyzedRevision = rs.getString("last_analyzed_revision");
            }
        } catch (SQLException ex) {
            throw new DatabaseAccessException("Could not close the stream used to retrieve the content of the binary-index field\n" + ex);
        } finally {
            connectionPool.releaseConnection(conn);
        }
        return lastAnalyzedRevision;
    }

    /**
     * returns the index of the file as an AstNode that contains all child nodes
     * @param filePath the path of the file
     * @param repository the name of the repository holding the file
     * @return the binary index of the file
     * @throws DatabaseAccessException
     */
    public static AstNode getBinaryIndexForFile(String filePath, String repository) throws DatabaseAccessException {
        if (connectionPool == null) {
            setupConnections();
        }
        ObjectInputStream regObjectStream = null;
        AstNode binaryIndex = null;
        Connection conn = connectionPool.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(STMT_GET_BINARY_INDEX_FOR_FILE);
            ps.setString(1, filePath);
            ps.setString(2, repository);
            ResultSet rs = ps.executeQuery();
            rs.first();
            byte[] regBytes = rs.getBytes("binary_index");
            if (regBytes == null) {
                return null;
            }
            ByteArrayInputStream regArrayStream = new ByteArrayInputStream(regBytes);
            regObjectStream = new ObjectInputStream(regArrayStream);
            binaryIndex = (AstNode) regObjectStream.readObject();
        } catch (ClassNotFoundException ex) {
            throw new DatabaseAccessException("The content of the blob storing the binary index of file " + filePath + " repository " + repository + " could not be parsed to an Object, the database content is probably corrupt");
        } catch (IOException ex) {
            throw new DatabaseAccessException("Could not create a ByteArrayInputStream from the content of the binaryIndex field in the database");
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while accessing the DB\n" + ex);
        } finally {
            connectionPool.releaseConnection(conn);
            try {
                if (regObjectStream != null) {
                    regObjectStream.close();
                }
            } catch (IOException ex) {
                throw new DatabaseAccessException("Could not close the stream used to retrieve the content of the binary-index field\n" + ex);
            }
        }
        return binaryIndex;
    }

    /**
     * creates an entry for the repository in the database
     * @param repositoryName the name of the repository
     * @throws DatabaseAccessException
     */
    public static void createRepositoryEntry(String repositoryName) throws DatabaseAccessException {
        if (connectionPool == null) {
            setupConnections();
        }
        Connection conn = connectionPool.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(STMT_CREATE_REPOSITORY_ENTRY);
            ps.setString(1, repositoryName);
            ps.execute();
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        } finally {
            connectionPool.releaseConnection(conn);
        }
    }

    /**
     * sets the last_analysis_revision of the repository in the database
     * @param repositoryName the name of the repository
     * @param revision the revision
     * @throws DatabaseAccessException
     */
    public static void setLastAnalyzedRevisionOfRepository(String repositoryName, String revision) throws DatabaseAccessException {
        if (connectionPool == null) {
            setupConnections();
        }
        Connection conn = connectionPool.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(STMT_SET_LAST_ANALYZED_REVISION_OF_REPOSITORY);
            ps.setString(1, revision);
            ps.setString(2, repositoryName);
            ps.execute();
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        } finally {
            connectionPool.releaseConnection(conn);
        }
    }

    /**
     * gets the id of the repository with the given name
     * @param repoName the name of the repository
     * @return the id if the repository
     * @throws DatabaseAccessException
     */
    private static int getRepoIdForRepoName(String repoName) throws DatabaseAccessException {
        if (connectionPool == null) {
            setupConnections();
        }
        int repoId = 0;

        Connection conn = connectionPool.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(STMT_GET_REPO_ID_FOR_NAME);
            ps.setString(1, repoName);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            rs.first();
            repoId = rs.getInt("repository_id");
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        } finally {
            connectionPool.releaseConnection(conn);
        }
        return repoId;
    }

    /**
     * returns a map of all files that import the target file
     * @param targetFileName the file that is imported in the found files
     * @param targetFileRepoName the name of the repository containing the target file
     * @return a map consisting of the name of the file as key and the binary index as value
     */
    public static Map<String, AstNode> getFilesImportingTargetFile(String targetFileName, String targetRepositoryName) throws DatabaseAccessException {
        Map<String, AstNode> files = new HashMap<String, AstNode>();
        if (connectionPool == null) {
            setupConnections();
        }
        Connection conn = connectionPool.getConnection();
        ObjectInputStream regObjectStream;
        try {
            String targetPackageName = targetFileName.substring(0, targetFileName.lastIndexOf(".")) + ".*";
            PreparedStatement ps = conn.prepareStatement(STMT_GET_FILES_IMPORTING_FILE);
            ps.setString(1, targetFileName);
            ps.setString(2, targetPackageName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                byte[] regBytes = rs.getBytes("binary_index");
                ByteArrayInputStream regArrayStream = new ByteArrayInputStream(regBytes);
                regObjectStream = new ObjectInputStream(regArrayStream);
                AstNode binaryIndex = (AstNode) regObjectStream.readObject();
                files.put(rs.getString("file_path"), binaryIndex);
            }
        } catch (ClassNotFoundException ex) {
            throw new DatabaseAccessException("The content of the blob storing the binary_index of a file could not be parsed to an Object, the database content is probably corrupt");
        } catch (IOException ex) {
            throw new DatabaseAccessException("Could not create a ByteArrayInputStream from the content of the binaryIndex field in the database");
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        } finally {
            connectionPool.releaseConnection(conn);
        }
        return files;
    }

    /**
     * sets all data created by the CodeAnalyzerPlugin for the file
     * @param filePath the path of the file
     * @param repository the repository holding the file
     * @param binaryIndex the binary index as an AstNode
     * @param usages the usages as a list of Usages
     * @param types the types that are declared in the file as a list of strings
     * @param imports the imports that are declared in the file as a list of strings
     * @throws DatabaseAccessException
     */
    public static void setAnalysisDataForFile(String filePath, String repository, AstNode binaryIndex, List<Usage> usages, List<String> types, List<String> imports) throws DatabaseAccessException {
        if (connectionPool == null) {
            setupConnections();
        }
        int fileId = ensureThatRecordExists(filePath, repository);
        int repoId = getRepoIdForRepoName(repository);
        Connection conn = connectionPool.getConnection();
        try {
            //Clears all types set for the file in a previous indexing process
            PreparedStatement ps = conn.prepareStatement(STMT_CLEAR_TYPES_FOR_FILE);
            ps.setInt(1, fileId);
            ps.execute();

            //Clears all imports set for the file in a previous indexing procedure
            ps = conn.prepareStatement(STMT_CLEAR_IMPORTS_FOR_FILE);
            ps.setInt(1, fileId);
            ps.execute();

            //Sets the Binary index and the usages for the file
            ps = conn.prepareStatement(STMT_SET_BINARY_INDEX_AND_USAGES_FOR_FILE);
            ps.setObject(1, binaryIndex);
            ps.setObject(2, usages);
            ps.setInt(3, fileId);
            ps.executeUpdate();

            //Sets the types for the file, if any
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
                ps.execute();
            }

            //Sets the imports for the file, if any
            if (!imports.isEmpty()) {
                //first build the import statement by adding all values
                String importString = STMT_SET_IMPORTS_FOR_FILE;
                for (int i = 0; i < imports.size(); i++) {
                    importString += "(" + fileId + ", ?), ";
                }
                importString = importString.substring(0, importString.length() - 2);
                ps = conn.prepareStatement(importString);
                for (int i = 0; i < imports.size(); i++) {
                    ps.setString(i + 1, imports.get(i));
                }
                ps.execute();
            }
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        } finally {
            connectionPool.releaseConnection(conn);
        }
    }

    /**
     * returns the id for the file
     * @param filePath the complete name of the file
     * @param repoId the id of the repository holding the file
     * @return the id if the file, or -1 if it wasn't found
     * @throws DatabaseAccessException
     */
    private static int getFileIdForFileName(String filePath, int repoId) throws DatabaseAccessException {
        if (connectionPool == null) {
            setupConnections();
        }
        int fileId = -1;
        Connection conn = connectionPool.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(STMT_GET_FILE_ID_FOR_FILE_NAME);
            ps.setString(1, filePath);
            ps.setInt(2, repoId);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                fileId = rs.getInt("file_id");
            }
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        } finally {
            connectionPool.releaseConnection(conn);
        }
        return fileId;
    }

    /**
     * returns the id of the file with the path in the given repository
     * if the there is no record for the file with this path and repository creates a new record and returns the id
     * @param filePath the path of the file
     * @param repository the name of the repository holding the file
     * @return the id of the record
     * @throws DatabaseAccessException
     */
    public static int ensureThatRecordExists(String filePath, String repository) throws DatabaseAccessException {
        if (connectionPool == null) {
            setupConnections();
        }
        int fileId = 0;
        int repoId = 0;
        Connection conn = connectionPool.getConnection();
        try {
            repoId = getRepoIdForRepoName(repository);
            fileId = getFileIdForFileName(filePath, repoId);
            if (fileId == -1) {
                //In case no record for this data exists
                PreparedStatement ps = conn.prepareStatement(STMT_CREATE_FILE_RECORD, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, filePath);
                ps.setString(2, repository);
                ps.execute();
                ResultSet generatedKeys = ps.getGeneratedKeys();
                generatedKeys.first();
                fileId = generatedKeys.getInt(1);
            }
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        } finally {
            connectionPool.releaseConnection(conn);
        }
        return fileId;
    }
}
