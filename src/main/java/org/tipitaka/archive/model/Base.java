package org.tipitaka.archive.model;

import java.util.Map;

/**
 * Created by christian on 03.09.16.
 */
class Base {

    protected final String version;
    private final String path;

    private final Script script;

    Base(Script script, String path, String version) {
        this.script = script;
        this.path = path;
        this.version = version;
    }

    public String getPath() {
        return path;
    }

    public Script getScript() {
        return script;
    }

    public String getVersion() {
        return version;
    }
}
