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

import java.util.List;
import java.util.Map;
import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.ExternalUsage;
import org.codesearch.commons.plugins.codeanalyzing.ast.Usage;

/**
 * Provides a connection to the database.
 * @author Samuel Kogler
 */
public interface DBAccess {

    /**
     * deletes all entries from the file and type table that are associated to the repository
     * @param repoName the name of the repository
     * @throws DatabaseAccessException
     */
    void clearTablesForRepository(String repoName) throws DatabaseAccessException;

    /**
     * creates an entry for the repository in the database
     * @param repositoryName the name of the repository
     * @throws DatabaseAccessException
     */
    void createRepositoryEntry(String repositoryName) throws DatabaseAccessException;

    /**
     * returns the id of the file with the path in the given repository
     * if the there is no record for the file with this path and repository creates a new record and returns the id
     * @param filePath the path of the file
     * @param repository the name of the repository holding the file
     * @return the id of the record
     * @throws DatabaseAccessException
     */
    int ensureThatRecordExists(String filePath, String repository) throws DatabaseAccessException;

    /**
     * returns the index of the file as an AstNode that contains all child nodes
     * @param filePath the path of the file
     * @param repository the name of the repository holding the file
     * @return the binary index of the file
     * @throws DatabaseAccessException
     */
    AstNode getBinaryIndexForFile(String filePath, String repository) throws DatabaseAccessException;

    /**
     * retrieves the path of the file in which the from the database
     * @param filePath the file path + name of the file
     * @param repository the name of the repository holding the file
     * @return the imports as a list of strings
     * @throws DatabaseAccessException
     */
    String getFilePathForTypeDeclaration(String fullyQualifiedName, String repository) throws DatabaseAccessException;

    /**
     * retrieves the file path of the file that contains the class
     * tries with all possible packages
     * @param className the name of class
     * @param repository the repository that holds the file
     * @param asteriskImports the packages that could contain the fiel
     * @return the path of the file
     * @throws DatabaseAccessException
     */
    String getFilePathForTypeDeclaration(String className, String repository, List<String> asteriskImports) throws DatabaseAccessException;

    /**
     * returns a map of all files that import the target file
     * @param targetFileName the file that is imported in the found files
     * @param targetFileRepoName the name of the repository containing the target file
     * @return a map consisting of the name of the file as key and the binary index as value
     */
    Map<String, AstNode> getFilesImportingTargetFile(String targetFileName, String targetRepositoryName) throws DatabaseAccessException;

    /**
     * retrieves the imports as strings from the database
     * @param filePath the file path + name of the file
     * @param repository the name of the repository holding the file
     * @return the imports as a list of strings
     * @throws DatabaseAccessException
     */
    List<String> getImportsForFile(String filePath, String repository) throws DatabaseAccessException;

    /**
     * returns the revision number of the repository that was valid during the last code analysis process
     * @param repositoryName the repository
     * @return the revision
     * @throws DatabaseAccessException
     */
    String getLastAnalyzedRevisionOfRepository(String repositoryName) throws DatabaseAccessException;

    /**
     * returns the usage at the given id for the file
     * @param usageId the id of the usage in the file
     * @param filePath the full name of the file
     * @param repository the name of the repository holding the file
     * @return the Usage
     * @throws DatabaseAccessException
     */
    ExternalUsage getUsageForIdInFile(int usageId, String filePath, String repository) throws DatabaseAccessException;

    /**
     * retrieves the usages in the file from the database
     * @param filePath the file path + name of the file
     * @param repository the name of the repository holding the file
     * @return the usages ordered by line number / column
     * @throws DatabaseAccessException
     */
    List<Usage> getUsagesForFile(String filePath, String repository) throws DatabaseAccessException;

    void purgeDatabaseEntries() throws DatabaseAccessException;

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
    void setAnalysisDataForFile(String filePath, String repository, AstNode binaryIndex, List<Usage> usages, List<String> types, List<String> imports) throws DatabaseAccessException;

    /**
     * sets the last_analysis_revision of the repository in the database
     * @param repositoryName the name of the repository
     * @param revision the revision
     * @throws DatabaseAccessException
     */
    void setLastAnalyzedRevisionOfRepository(String repositoryName, String revision) throws DatabaseAccessException;

    void deleteFile(String filePath, String repository) throws DatabaseAccessException;

}
