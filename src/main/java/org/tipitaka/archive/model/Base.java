package org.tipitaka.archive.model;

import java.util.Map;

/**
 * Created by christian on 03.09.16.
 */
class Base {

    private final String path;

    Base(String path) {
        this.path = path == null || path.length() == 0 ? null : path;
    }

    public String getPath() {
        return path;
    }

    void toString(StringBuilder builder) {
        builder.append(path);
    }
}
