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
package org.codesearch.indexer.tasks;

import java.util.List;
import java.util.Map;

import org.codesearch.commons.database.DBAccess;
import org.codesearch.commons.database.DatabaseAccessException;
import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.ExternalUsage;
import org.codesearch.commons.plugins.codeanalyzing.ast.Usage;

/**
 * Dummy DBAccess class that provides no functionality at all
 * @author Samuel Kogler
 */
public class NoOpDatabaseImpl implements DBAccess {

    @Override
    public void clearTablesForRepository(String repoName) throws DatabaseAccessException {

    }

    @Override
    public void createRepositoryEntry(String repositoryName) throws DatabaseAccessException {

    }

    @Override
    public int ensureThatRecordExists(String filePath, String repository) throws DatabaseAccessException {
        return 0;
    }

    @Override
    public AstNode getBinaryIndexForFile(String filePath, String repository) throws DatabaseAccessException {
        return null;
    }

    @Override
    public String getFilePathForTypeDeclaration(String fullyQualifiedName, String repository) throws DatabaseAccessException {
        return null;
    }

    @Override
    public String getFilePathForTypeDeclaration(String className, String repository, List<String> asteriskImports) throws DatabaseAccessException {
        return null;
    }

    @Override
    public Map<String, AstNode> getFilesImportingTargetFile(String targetFileName, String targetRepositoryName) throws DatabaseAccessException {
        return null;
    }

    @Override
    public List<String> getImportsForFile(String filePath, String repository) throws DatabaseAccessException {
        return null;
    }

    @Override
    public String getLastAnalyzedRevisionOfRepository(String repositoryName) throws DatabaseAccessException {
        return "0";
    }

    @Override
    public ExternalUsage getUsageForIdInFile(int usageId, String filePath, String repository) throws DatabaseAccessException {
        return null;
    }

    @Override
    public List<Usage> getUsagesForFile(String filePath, String repository) throws DatabaseAccessException {
        return null;
    }

    @Override
    public void purgeDatabaseEntries() throws DatabaseAccessException {
    }

    @Override
    public void setAnalysisDataForFile(String filePath, String repository, AstNode binaryIndex, List<Usage> usages, List<String> types, List<String> imports) throws DatabaseAccessException {
    }

    @Override
    public void setLastAnalyzedRevisionOfRepository(String repositoryName, String revision) throws DatabaseAccessException {
    }

    @Override
    public void deleteFile(String filePath, String repository) throws DatabaseAccessException {
    }
}
