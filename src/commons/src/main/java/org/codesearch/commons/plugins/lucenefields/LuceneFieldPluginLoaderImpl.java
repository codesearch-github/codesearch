package org.codesearch.commons.plugins.lucenefields;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.codesearch.commons.constants.IndexConstants;
import org.codesearch.commons.plugins.PluginLoader;

/**
 * Implements {@link LuceneFieldPluginLoader}
 * 
 * @author Samuel Kogler
 */
public class LuceneFieldPluginLoaderImpl implements LuceneFieldPluginLoader {

    private List<LuceneFieldPlugin> luceneFieldPlugins = new LinkedList<LuceneFieldPlugin>();
    private PerFieldAnalyzerWrapper caseSensitivePfaw = new PerFieldAnalyzerWrapper(new SimpleSourceCodeAnalyzer(true));
    private PerFieldAnalyzerWrapper caseInsensitivePfaw = new PerFieldAnalyzerWrapper(new SimpleSourceCodeAnalyzer(false));
    private static final Logger LOG = Logger.getLogger(LuceneFieldPluginLoader.class);

    @Inject
    public LuceneFieldPluginLoaderImpl(PluginLoader pluginLoader) {
        luceneFieldPlugins = pluginLoader.getAllPluginsOfClass(LuceneFieldPlugin.class);
        if (luceneFieldPlugins.isEmpty()) {
            LOG.warn("No LuceneFieldPlugins could be found. Indexing will not create a usable index");
        }

        for (LuceneFieldPlugin currentPlugin : luceneFieldPlugins) {
            caseSensitivePfaw.addAnalyzer(currentPlugin.getFieldName(), currentPlugin.getRegularCaseAnalyzer());
            caseInsensitivePfaw.addAnalyzer(currentPlugin.getFieldName(), currentPlugin.getRegularCaseAnalyzer());
            caseSensitivePfaw.addAnalyzer(currentPlugin.getFieldName() + IndexConstants.LC_POSTFIX, currentPlugin.getLowerCaseAnalyzer());
            caseInsensitivePfaw.addAnalyzer(currentPlugin.getFieldName() + IndexConstants.LC_POSTFIX, currentPlugin.getLowerCaseAnalyzer());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PerFieldAnalyzerWrapper getPerFieldAnalyzerWrapper(boolean caseSensitive) {
        return caseSensitive ? caseSensitivePfaw : caseInsensitivePfaw;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LuceneFieldPlugin> getAllLuceneFieldPlugins() {
        return luceneFieldPlugins;
    }
}
