package org.codesearch.searcher.shared;

import java.io.Serializable;

/**
 * Describes a field that can be used in the query.
 * @author Samuel Kogler
 */
public class SearchField implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;

    public SearchField() {
    }

    public SearchField(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}
