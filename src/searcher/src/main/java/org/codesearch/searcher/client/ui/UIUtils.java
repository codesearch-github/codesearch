package org.codesearch.searcher.client.ui;

/**
 * Some very basic helper methods.
 * @author Samuel Kogler
 */
public class UIUtils {

    private UIUtils() {
    }

    public static String escape(String term) {
        term = term.replaceAll(UIConstants.URL_TOKEN_SEPARATOR, "%26");
        term = term.replaceAll(",", "%2C");
        return term;
    }

    public static String unescape(String term) {
        term = term.replaceAll("%26", UIConstants.URL_TOKEN_SEPARATOR);
        term = term.replaceAll("%2C", ",");
        return term;
    }
}
