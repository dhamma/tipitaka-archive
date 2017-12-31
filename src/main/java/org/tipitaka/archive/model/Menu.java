package org.tipitaka.archive.model;

import java.util.Map;

/**
 * Created by christian on 03.09.16.
 */
public class Menu extends Base {

    private final Map<String, String> items;

    public Menu(String path, Map<String, String> items) {
        super(path);
        this.items = items;
    }

    public Map<String, String> getItems() {
        return items;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        toString(builder);
        builder.append(":").append(items);
        return builder.toString();
    }
}
