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
package org.codesearch.searcher.server.util;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;

/**
 * This
 *
 * @author Stephan Stiboller
 * */
public class STAlternativeSuggestor {

    private String defaultField = "content";
    private Directory spellIndexDirectory;

    public STAlternativeSuggestor(String defaultField, Directory spellIndexDirectory) {
        this.defaultField = defaultField;
        this.spellIndexDirectory = spellIndexDirectory;
    }

    /**
     * Returns a list of autocomplete suggestions.
     * @param queryString
     * @return String[] of completion suggestions
     * @throws IOException
     */
    public List<String> suggest(String queryString) throws IOException {
        SpellChecker spellChecker = new SpellChecker(spellIndexDirectory);
        String[] spresult = spellChecker.suggestSimilar(queryString, 20);
        if (spresult.length == 0) {
            return null;
        }
        LinkedList<String> suggestions = new LinkedList<String>();
        for(int i = 0; i < spresult.length; i ++ )
            suggestions.add(spresult[i]);

        return suggestions;
    }

    /**
     * Creates the spell index for the SpellChecker
     * @param field
     * @param originalIndexDirectory
     * @param spellIndexDirectory
     * @throws IOException
     */
    public void createSpellIndex(String field, Directory originalIndexDirectory, Directory spellIndexDirectory) throws IOException {
        IndexReader indexReader = null;
        try {
            indexReader = IndexReader.open(originalIndexDirectory);
            Dictionary dictionary = new LuceneDictionary(indexReader, field);
            SpellChecker spellChecker = new SpellChecker(spellIndexDirectory);
            spellChecker.indexDictionary(dictionary);
        } finally {
            if (indexReader != null) {
                indexReader.close();
            }
        }
    }

    /**
     * Creates the default spell index.
     * @param originalIndexDirectory
     * @param spellIndexDirectory
     * @throws IOException
     */
     public void createDefaultSpellIndex(Directory originalIndexDirectory, Directory spellIndexDirectory) throws IOException {
         createSpellIndex(defaultField, originalIndexDirectory, spellIndexDirectory);
     }
}
