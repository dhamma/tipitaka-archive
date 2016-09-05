package org.tipitaka.archive.model;

import java.util.Map;

/**
 * Created by christian on 03.09.16.
 */
public class Menu extends Base {

    private final boolean hasDocuments;

    protected final Map<String, String> items;

    public Menu(Script script, String path, String version, boolean hasLeaves, Map<String, String> items) {
        super(script, path, version);
        this.hasDocuments = hasLeaves;
        this.items = items;
    }

    public Boolean hasDocuments() {
        return hasDocuments;
    }

    public Map<String, String> getItems() {
        return items;
    }
}
