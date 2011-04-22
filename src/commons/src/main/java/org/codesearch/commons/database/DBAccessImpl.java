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

import com.google.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.ExternalUsage;
import org.codesearch.commons.plugins.codeanalyzing.ast.Usage;

import com.mysql.jdbc.Statement;
import java.util.LinkedList;

/**
 * DBUtils provides methods for access to the database specified in the configuration
 * @author David Froehlich
 */
public class DBAccessImpl implements DBAccess {

    private static final Logger LOG = Logger.getLogger(DBAccessImpl.class);
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
    private static final String STMT_CLEAR_FILES_FOR_REPOSITORY = "DELETE FROM file WHERE repository_id = ?";
    private static final String STMT_RESET_REPOSITORY_REVISIONS = "UPDATE repository SET last_analyzed_revision = 0";
    private static final String STMT_PURGE_ALL_FILE_RECORDS = "DELETE FROM file";
    private static final String STMT_DELETE_FILE = "DELETE FROM file WHERE file_path = ? AND repository_id = (SELECT repository_id FROM repository where repository_name = ?)";
    private ConnectionPool connectionPool;

    @Inject
    public DBAccessImpl(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized ExternalUsage getUsageForIdInFile(int usageId, String filePath, String repository) throws DatabaseAccessException {
        try {
            ExternalUsage usage = null;
            List<Usage> usageList = getUsagesForFile(filePath, repository);
            try {
                usage = (ExternalUsage) usageList.get(usageId);
            } catch (ClassCastException ex) {
                throw new DatabaseAccessException("Usage at requested ID is no external usage");
            }
            return usage;
        } catch (DatabaseEntryNotFoundException ex) {
            throw new DatabaseAccessException("There are no usages stored for the file " + filePath + " in the repository " + repository);
        }
    }

    /** {@inheritDoc} */
    @Override
    public synchronized List<String> getImportsForFile(String filePath, String repository) throws DatabaseAccessException {
        List<String> imports = new LinkedList<String>();
        Connection conn = connectionPool.getConnection();
        try {
            int repo_id = getRepoIdForRepoName(repository);
            int file_id = getFileIdForFileName(filePath, repo_id);
            PreparedStatement ps = conn.prepareStatement("SELECT target_file_path FROM import WHERE source_file_id = ?");
            ps.setInt(1, file_id);

            ResultSet rs = ps.executeQuery();
            String currentImport = "";
            while (rs.next()) {
                currentImport = rs.getString("target_file_path");
                imports.add(currentImport);
            }
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        } finally {
            connectionPool.releaseConnection(conn);
        }
        return imports;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized List<Usage> getUsagesForFile(String filePath, String repository) throws DatabaseAccessException, DatabaseEntryNotFoundException {
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
            } else {
                throw new DatabaseEntryNotFoundException("There is no record for the file " + filePath + " in the repository " + repository + " it probably is in a repository that has code navigation disabled or is in an external library");
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

    /** {@inheritDoc} */
    @Override
    public synchronized String getFilePathForTypeDeclaration(String fullyQualifiedName, String repository) throws DatabaseAccessException {

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

    /** {@inheritDoc} */
    @Override
    public synchronized void clearTablesForRepository(String repoName) throws DatabaseAccessException {
        Connection conn = connectionPool.getConnection();
        try {
            int repoId = getRepoIdForRepoName(repoName);
            PreparedStatement ps = conn.prepareStatement(STMT_CLEAR_FILES_FOR_REPOSITORY);
            ps.setInt(1, repoId);
            ps.execute();
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        } finally {
            connectionPool.releaseConnection(conn);
        }
    }

    /** {@inheritDoc} */
    @Override
    public synchronized String getFilePathForTypeDeclaration(String className, String repository, List<String> asteriskImports) throws DatabaseAccessException {
        if (asteriskImports.isEmpty()) {
            return null;
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

    /** {@inheritDoc} */
    @Override
    public synchronized String getLastAnalyzedRevisionOfRepository(String repositoryName) throws DatabaseAccessException {
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

    /** {@inheritDoc} */
    @Override
    public synchronized AstNode getBinaryIndexForFile(String filePath, String repository) throws DatabaseAccessException, DatabaseEntryNotFoundException {
        ObjectInputStream regObjectStream = null;
        AstNode binaryIndex = null;
        Connection conn = connectionPool.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(STMT_GET_BINARY_INDEX_FOR_FILE);
            ps.setString(1, filePath);
            ps.setString(2, repository);
            ResultSet rs = ps.executeQuery();
            if (!rs.first()) {
                throw new DatabaseEntryNotFoundException("There is no record for the file " + filePath + " in the repository " + repository + " it probably is in a repository that has code navigation disabled or is in an external library");
            }
            byte[] regBytes = rs.getBytes("binary_index");
            if (regBytes == null) {
                throw new DatabaseEntryNotFoundException("The record for the file " + filePath + " in the repository " + repository + "does not contain a binary index");
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

    /** {@inheritDoc} */
    @Override
    public synchronized void createRepositoryEntry(String repositoryName) throws DatabaseAccessException {
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

    /** {@inheritDoc} */
    @Override
    public synchronized void setLastAnalyzedRevisionOfRepository(String repositoryName, String revision) throws DatabaseAccessException {
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

    /** {@inheritDoc} */
    @Override
    public synchronized void purgeDatabaseEntries() throws DatabaseAccessException {
        Connection conn = connectionPool.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(STMT_RESET_REPOSITORY_REVISIONS);
            ps.execute();
            ps = conn.prepareStatement(STMT_PURGE_ALL_FILE_RECORDS);
            ps.execute();
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        } finally {
            connectionPool.releaseConnection(conn);
        }
    }

    /** {@inheritDoc} */
    @Override
    public synchronized Map<String, AstNode> getFilesImportingTargetFile(String targetFileName, String targetRepositoryName) throws DatabaseAccessException {
        Map<String, AstNode> files = new HashMap<String, AstNode>();
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

    /** {@inheritDoc} */
    @Override
    public synchronized void setAnalysisDataForFile(String filePath, String repository, AstNode binaryIndex, List<Usage> usages, List<String> types, List<String> imports) throws DatabaseAccessException {
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

    /** {@inheritDoc} */
    @Override
    public synchronized int ensureThatRecordExists(String filePath, String repository) throws DatabaseAccessException {
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

    /** {@inheritDoc} */
    @Override
    public synchronized void deleteFile(String filePath, String repository) throws DatabaseAccessException {
        Connection conn = connectionPool.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(STMT_DELETE_FILE, Statement.NO_GENERATED_KEYS);
            ps.setString(1, filePath);
            ps.setString(2, repository);
            ps.execute();
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        }
    }

    /**
     * returns the id for the file
     * @param filePath the complete name of the file
     * @param repoId the id of the repository holding the file
     * @return the id if the file, or -1 if it wasn't found
     * @throws DatabaseAccessException
     */
    private int getFileIdForFileName(String filePath, int repoId) throws DatabaseAccessException {
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
     * gets the id of the repository with the given name
     * @param repoName the name of the repository
     * @return the id if the repository
     * @throws DatabaseAccessException
     */
    private int getRepoIdForRepoName(String repoName) throws DatabaseAccessException {
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
}
