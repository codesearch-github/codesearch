/**
 * Copyright 2010 David Froehlich <david.froehlich@businesssoftware.at>, Samuel
 * Kogler <samuel.kogler@gmail.com>, Stephan Stiboller <stistc06@htlkaindorf.at>
 *
 * This file is part of Codesearch.
 *
 * Codesearch is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Codesearch is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Codesearch. If not, see <http://www.gnu.org/licenses/>.
 */
package org.codesearch.commons.database;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.ExternalUsage;
import org.codesearch.commons.plugins.codeanalyzing.ast.Usage;
import org.codesearch.commons.plugins.vcs.VersionControlPlugin;

import com.google.inject.Inject;

/**
 * DBUtils provides methods for access to the database specified in the
 * configuration
 * 
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
    private static final String STMT_GET_FILE_ID_FOR_FILE_NAME = "SELECT file_id FROM file f JOIN repository r ON f.repository_id = r.repository_id WHERE f.file_path = ? AND r.repository_name = ?";
    private static final String STMT_CREATE_FILE_RECORD = "INSERT INTO file (file_path, repository_id) VALUES (?, (SELECT repository_id from repository where repository_name = ?))";
    private static final String STMT_GET_REPO_ID_FOR_NAME = "SELECT repository_id FROM repository where repository_name = ?";
    private static final String STMT_CREATE_TYPES_FOR_FILE = "INSERT INTO type (full_name, file_id, repo_id) VALUES ";
    private static final String STMT_GET_FILE_PATH_FOR_TYPE_DECLARATION = "SELECT f.file_path FROM file f JOIN type t ON f.file_id = t.file_id JOIN repository r ON f.repository_id = r.repository_id WHERE r.repository_name = ? AND t.full_name LIKE ?";
    private static final String STMT_GET_FILE_PATH_FOR_TYPE_DECLARATION_WITH_PACKAGES = "SELECT f.file_path FROM file f JOIN type t ON f.file_id = t.file_id JOIN repository r ON r.repository_id = f.repository_id WHERE r.repository_name = ? AND t.full_name IN (";
    private static final String STMT_GET_USAGES_FOR_FILE = "SELECT usages FROM file f JOIN repository r ON f.repository_id = r.repository_id WHERE f.file_path = ? AND r.repository_name = ?";
    private static final String STMT_DELETE_REPOSITORY = "DELETE FROM repository WHERE repository_name = ?";
    private static final String STMT_GET_IMPORTS_FOR_FILE = "SELECT target_file_path FROM import i JOIN file f ON i.source_file_id = f.file_id JOIN repository r ON f.repository_id = r.repository_id WHERE r.repository_name = ? AND f.file_path = ?";
    private static final String STMT_PURGE_ALL_RECORDS = "DELETE FROM repository";
    private static final String STMT_DELETE_FILE = "DELETE FROM file WHERE file_path = ? AND repository_id = (SELECT repository_id FROM repository where repository_name = ?)";

    private DataSource dataSource;

    @Inject
    public DBAccessImpl() {
        Connection testConnection = null;
        try {
            InitialContext ic = new InitialContext();
            Context evtContext = (Context)ic.lookup("java:comp/env/");
            dataSource = (DataSource)evtContext.lookup("jdbc/codesearch");
            if (dataSource != null) {
                testConnection = dataSource.getConnection();
                if (testConnection.isValid(3)) {
                    LOG.info("Successfully connected to database");
                }
            } else {
                LOG.error("Database is not configured, code analysis will not be available");
            }
        } catch (SQLException ex) {
            LOG.error("Error accessing database, code analysis will not be available:\n", ex);
        } catch (NamingException ex) {
            LOG.error("Error accessing database, code analysis will not be available:\n", ex);
        } finally {
            if (testConnection != null) {
                try {
                    testConnection.close();
                } catch (Exception ex) {
                    LOG.warn("Could not close test database connection:\n", ex);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized ExternalUsage getUsageForIdInFile(int usageId, String filePath, String repository)
            throws DatabaseAccessException {
        ExternalUsage usage = null;
        List<Usage> usageList = getUsagesForFile(filePath, repository);
        try {
            usage = (ExternalUsage)usageList.get(usageId);
        } catch (ClassCastException ex) {
            throw new DatabaseAccessException("Usage at requested ID is no external usage");
        }
        return usage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<String> getImportsForFile(String filePath, String repository)
            throws DatabaseAccessException {
        ResultSetHandler<List<String>> handler = new ResultSetHandler<List<String>>() {

            @Override
            public List<String> handle(ResultSet rs) throws SQLException {
                List<String> imports = new LinkedList<String>();
                while (rs.next()) {
                    imports.add(rs.getString("target_file_path"));
                }
                return imports;
            }
        };

        QueryRunner run = new QueryRunner(dataSource);
        try {
            return run.query(STMT_GET_IMPORTS_FOR_FILE, handler, repository, filePath);
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public synchronized List<Usage> getUsagesForFile(String filePath, String repository) throws DatabaseAccessException {
        ResultSetHandler<byte[]> h = new SingleValueByteArrayHandler();

        try {
            QueryRunner run = new QueryRunner(dataSource);
            byte[] result = run.query(STMT_GET_USAGES_FOR_FILE, h, filePath, repository);
            if (result != null) {
                ObjectInputStream regObjectStream = new ObjectInputStream(new ByteArrayInputStream(result));
                return (List<Usage>)regObjectStream.readObject();
            } else {
                return null;
            }
        } catch (IOException ex) {
            throw new DatabaseAccessException("The content of the blob storing the usages of the file " + filePath
                + " repository " + repository
                + " could not be parsed to an Object, the database content is probably corrupt");
        } catch (ClassNotFoundException ex) {
            throw new DatabaseAccessException("The content of the blob storing the usages of the file " + filePath
                + " repository " + repository
                + " could not be parsed to an Object, the database content is probably corrupt");
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized String getFilePathForTypeDeclaration(String fullyQualifiedName, String repository)
            throws DatabaseAccessException {
        ResultSetHandler<String> handler = new SingleValueStringHandler();
        QueryRunner run = new QueryRunner(dataSource);
        try {
            return run.query(STMT_GET_FILE_PATH_FOR_TYPE_DECLARATION, handler, repository, fullyQualifiedName);
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void deleteRepository(String repoName) throws DatabaseAccessException {
        QueryRunner run = new QueryRunner(dataSource);
        try {
            run.update(STMT_DELETE_REPOSITORY, repoName);
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized String getFilePathForTypeDeclaration(String className, String repository,
            List<String> asteriskImports) throws DatabaseAccessException {
        if (asteriskImports.isEmpty()) {
            return null;
        }
        ResultSetHandler<String> handler = new SingleValueStringHandler();
        StringBuilder importString = new StringBuilder();
        for (String currentImport : asteriskImports) {
            importString.append("'").append(currentImport.substring(0, currentImport.length() - 1)).append(className)
                .append("',");
        }
        importString.deleteCharAt(importString.length() - 1);
        importString.append(")");
        QueryRunner run = new QueryRunner(dataSource);
        try {
            return run.query(STMT_GET_FILE_PATH_FOR_TYPE_DECLARATION_WITH_PACKAGES + importString.toString(), handler,
                repository);
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized String getLastAnalyzedRevisionOfRepository(String repositoryName)
            throws DatabaseAccessException {
        ResultSetHandler<String> handler = new SingleValueStringHandler();
        QueryRunner run = new QueryRunner(dataSource);
        try {
            String result = run.query(STMT_GET_LAST_ANALYZED_REVISION_OF_REPOSITORY, handler, repositoryName);
            if (StringUtils.isEmpty(result)) {
                createRepositoryEntry(repositoryName);
                return VersionControlPlugin.UNDEFINED_VERSION;
            } else {
                return result;
            }
        } catch (SQLException ex) {
            throw new DatabaseAccessException(
                "Could not close the stream used to retrieve the content of the binary-index field\n" + ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized AstNode getBinaryIndexForFile(String filePath, String repository)
            throws DatabaseAccessException {
        ObjectInputStream regObjectStream = null;
        SingleValueByteArrayHandler h = new SingleValueByteArrayHandler();
        QueryRunner run = new QueryRunner(dataSource);

        try {
            byte[] result = run.query(STMT_GET_BINARY_INDEX_FOR_FILE, h, filePath, repository);
            if (result != null) {
                ByteArrayInputStream regArrayStream = new ByteArrayInputStream(result);
                regObjectStream = new ObjectInputStream(regArrayStream);
                return (AstNode)regObjectStream.readObject();
            } else {
                return null;
            }
        } catch (ClassNotFoundException ex) {
            throw new DatabaseAccessException("The content of the blob storing the binary index of file " + filePath
                + " repository " + repository
                + " could not be parsed to an Object, the database content is probably corrupt");
        } catch (IOException ex) {
            throw new DatabaseAccessException(
                "Could not create a ByteArrayInputStream from the content of the binaryIndex field in the database");
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while accessing the DB\n" + ex);
        } finally {
            try {
                if (regObjectStream != null) {
                    regObjectStream.close();
                }
            } catch (IOException ex) {
                throw new DatabaseAccessException(
                    "Could not close the stream used to retrieve the content of the binary-index field\n" + ex);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void createRepositoryEntry(String repositoryName) throws DatabaseAccessException {
        QueryRunner run = new QueryRunner(dataSource);
        try {
            run.update(STMT_CREATE_REPOSITORY_ENTRY, repositoryName);
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void setLastAnalyzedRevisionOfRepository(String repositoryName, String revision)
            throws DatabaseAccessException {
        QueryRunner run = new QueryRunner(dataSource);
        try {
            run.update(STMT_SET_LAST_ANALYZED_REVISION_OF_REPOSITORY, revision, repositoryName);
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void purgeDatabaseEntries() throws DatabaseAccessException {
        QueryRunner run = new QueryRunner(dataSource);
        try {
            run.update(STMT_PURGE_ALL_RECORDS);
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Map<String, AstNode> getFilesImportingTargetFile(String targetFileName,
            String targetRepositoryName) throws DatabaseAccessException {
        Map<String, AstNode> files = new HashMap<String, AstNode>();
        ObjectInputStream regObjectStream;
        ResultSetHandler<Map<String, byte[]>> h = new ResultSetHandler<Map<String, byte[]>>() {

            @Override
            public Map<String, byte[]> handle(ResultSet rs) throws SQLException {
                Map<String, byte[]> results = new HashMap<String, byte[]>();

                while (rs.next()) {
                    results.put(rs.getString(1), rs.getBytes(2));
                }
                return results;
            }
        };

        QueryRunner run = new QueryRunner(dataSource);
        String targetPackageName = targetFileName.substring(0, targetFileName.lastIndexOf('.')) + ".*";

        try {
            Map<String, byte[]> results = run
                .query(STMT_GET_FILES_IMPORTING_FILE, h, targetFileName, targetPackageName);
            for (Map.Entry<String, byte[]> entry : results.entrySet()) {
                ByteArrayInputStream regArrayStream = new ByteArrayInputStream(entry.getValue());
                regObjectStream = new ObjectInputStream(regArrayStream);
                AstNode binaryIndex = (AstNode)regObjectStream.readObject();
                files.put(entry.getKey(), binaryIndex);
            }
        } catch (ClassNotFoundException ex) {
            throw new DatabaseAccessException(
                "The content of the blob storing the binary_index of a file could not be parsed to an Object, the database content is probably corrupt");
        } catch (IOException ex) {
            throw new DatabaseAccessException(
                "Could not create a ByteArrayInputStream from the content of the binaryIndex field in the database");
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        }
        return files;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void setAnalysisDataForFile(String filePath, String repository, AstNode binaryIndex,
            List<Usage> usages, List<String> types, List<String> imports) throws DatabaseAccessException {
        int fileId = ensureThatRecordExists(filePath, repository);
        int repoId = getRepoIdForRepoName(repository);

        QueryRunner run = new QueryRunner(dataSource);
        try {
            // Clears all types set for the file in a previous indexing process
            run.update(STMT_CLEAR_TYPES_FOR_FILE, fileId);
            // Clears all imports set for the file in a previous indexing procedure
            run.update(STMT_CLEAR_IMPORTS_FOR_FILE, fileId);
            // Sets the Binary index and the usages for the file
            run.update(STMT_SET_BINARY_INDEX_AND_USAGES_FOR_FILE, binaryIndex, usages, fileId);
            // Sets the types for the file, if any
            if (!(types.isEmpty())) {
                // extends the insert into statement by one line for each type in the list of types
                StringBuilder statement = new StringBuilder(STMT_CREATE_TYPES_FOR_FILE);
                String[] params = new String[types.size()];
                for (int i = 0; i < types.size(); i++) {
                    statement.append("(?, ").append(fileId).append(", ").append(repoId).append("),");
                    params[i] = types.get(i);
                }
                statement.deleteCharAt(statement.length() - 1);
                run.update(statement.toString(), (Object[])params);
            }
            // Sets the imports for the file, if any
            if (!imports.isEmpty()) {
                // first build the import statement by adding all values
                StringBuilder importString = new StringBuilder(STMT_SET_IMPORTS_FOR_FILE);
                String[] params = new String[imports.size()];
                for (int i = 0; i < imports.size(); i++) {
                    importString.append("(").append(fileId).append(", ?),");
                    params[i] = imports.get(i);
                }
                importString.deleteCharAt(importString.length() - 1);
                run.update(importString.toString(), (Object[])params);
            }
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized int ensureThatRecordExists(String filePath, String repository) throws DatabaseAccessException {
        int fileId = getFileIdForFileName(filePath, repository);
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            if (fileId == -1) {
                conn = dataSource.getConnection();
                // In case no record for this data exists
                statement = conn.prepareStatement(STMT_CREATE_FILE_RECORD, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, filePath);
                statement.setString(2, repository);
                statement.execute();
                ResultSet generatedKeys = statement.getGeneratedKeys();
                generatedKeys.first();
                fileId = generatedKeys.getInt(1);
            }
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                }
            }
        }
        return fileId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void deleteFile(String filePath, String repository) throws DatabaseAccessException {
        QueryRunner run = new QueryRunner(dataSource);
        try {
            run.update(STMT_DELETE_FILE, filePath, repository);
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        }
    }

    /**
     * returns the id for the file
     * 
     * @param filePath the complete name of the file
     * @param repoId the id of the repository holding the file
     * @return the id if the file, or -1 if it wasn't found
     * @throws DatabaseAccessException
     */
    private int getFileIdForFileName(String filePath, String repository) throws DatabaseAccessException {
        int fileId = -1;
        QueryRunner run = new QueryRunner(dataSource);
        SingleValueStringHandler h = new SingleValueStringHandler();
        try {
            String result = run.query(STMT_GET_FILE_ID_FOR_FILE_NAME, h, filePath, repository);
            if (StringUtils.isNotEmpty(result)) {
                fileId = Integer.parseInt(result);
            }
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        }
        return fileId;
    }

    /**
     * gets the id of the repository with the given name
     * 
     * @param repoName the name of the repository
     * @return the id if the repository, or -1 if not found
     * @throws DatabaseAccessException
     */
    private int getRepoIdForRepoName(String repoName) throws DatabaseAccessException {
        int repoId = -1;

        QueryRunner run = new QueryRunner(dataSource);
        SingleValueStringHandler h = new SingleValueStringHandler();
        try {
            String result = run.query(STMT_GET_REPO_ID_FOR_NAME, h, repoName);
            if (StringUtils.isNotEmpty(result)) {
                repoId = Integer.parseInt(result);
            }
        } catch (SQLException ex) {
            throw new DatabaseAccessException("SQLException while trying to access the database\n" + ex);
        }
        return repoId;
    }

    private class SingleValueStringHandler implements ResultSetHandler<String> {

        @Override
        public String handle(ResultSet rs) throws SQLException {
            if (rs.next()) {
                return rs.getString(1);
            } else {
                return null;
            }
        }
    }

    private class SingleValueByteArrayHandler implements ResultSetHandler<byte[]> {

        @Override
        public byte[] handle(ResultSet rs) throws SQLException {
            if (rs.next()) {
                return rs.getBytes(1);
            } else {
                return null;
            }
        }
    }
}
