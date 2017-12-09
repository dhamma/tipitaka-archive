package org.tipitaka.archive.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by christian on 03.09.16.
 */
class ExtendedBase extends Base {

    private final Script script;
    private final String version;
    private final String baseUrl;
    private final List<String> titlePath;

    ExtendedBase(String path, Script script, String version, String baseUrl, List<String> titlePath) {
        super(path);
        this.script = script;
        this.version = version;
        this.baseUrl = baseUrl;
        this.titlePath = titlePath;
    }

    public Script getScript() {
        return script;
    }

    public String getVersion() {
        return version;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public List<String> getTitlePath() {
        return titlePath;
    }
}
