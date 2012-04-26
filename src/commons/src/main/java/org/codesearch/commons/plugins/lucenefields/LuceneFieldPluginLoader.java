package org.codesearch.commons.plugins.lucenefields;

import java.util.List;

import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;

/**
 * Helps with loading {@link LuceneFieldPlugin}s by creating 
 * a {@link PerFieldAnalyzerWrapper} for all available plugins.
 * @author Samuel Kogler
 */
public interface LuceneFieldPluginLoader {
    /**
     * Returns a {@link PerFieldAnalyzerWrapper} for all loaded lucene field plugins.
     * @param caseSensitive Whether the default analyzer should be case sensitive.
     * @return The wrapper.
     */
    PerFieldAnalyzerWrapper getPerFieldAnalyzerWrapper(boolean caseSensitive);
    
    /**
     * Returns a list of all the loaded {@link LuceneFieldPlugin}s.
     * @return The list of plugins.
     */
    List<LuceneFieldPlugin> getAllLuceneFieldPlugins();
}
