package org.codesearch.indexer.tasks;

import java.util.List;
import java.util.Map;

import org.codesearch.commons.database.DBAccess;
import org.codesearch.commons.database.DatabaseAccessException;
import org.codesearch.commons.database.DatabaseEntryNotFoundException;
import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.ExternalUsage;
import org.codesearch.commons.plugins.codeanalyzing.ast.Usage;

/**
 *
 * @author Samuel Kogler
 */
public class MockDatabaseImpl implements DBAccess {
    
    @Override
    public void clearTablesForRepository(String repoName) throws DatabaseAccessException {
        System.out.println("clearing repo: " + repoName);
    }

    @Override
    public void createRepositoryEntry(String repositoryName) throws DatabaseAccessException {

    }

    @Override
    public int ensureThatRecordExists(String filePath, String repository) throws DatabaseAccessException {
        return 0;
    }

    @Override
    public AstNode getBinaryIndexForFile(String filePath, String repository) throws DatabaseAccessException, DatabaseEntryNotFoundException {
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
    public List<Usage> getUsagesForFile(String filePath, String repository) throws DatabaseAccessException, DatabaseEntryNotFoundException {
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
