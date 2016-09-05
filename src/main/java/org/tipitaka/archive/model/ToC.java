package org.tipitaka.archive.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by christian on 03.09.16.
 */
public class ToC extends ExtendedBase {

    private final boolean hasDocuments;

    protected final Map<String, String> items;

    public ToC(Script script, String path, String version, List<String> titlePath, Map<String, String> items, boolean hasDocuments,
               Menu... menus) {
        super(script, path, version, titlePath, menus);
        this.hasDocuments = hasDocuments;
        this.items = items;
    }

    public Boolean hasDocuments() {
        return hasDocuments;
    }

    public Map<String, String> getItems() {
        return items;
    }

}
