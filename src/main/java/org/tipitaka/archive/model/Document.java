package org.tipitaka.archive.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/**
 * Created by christian on 03.09.16.
 */
public class Document extends ExtendedBase {
    private final String normativeSource;
    private final String source;
    private final List<String> versions;

    @JsonCreator
    public Document(Script script, String path, String version, List<String> titlePath, String normativeSource,
                    String source, List<String> versions, Menu... menus) {
        super(script, path, version, titlePath, menus);
        this.normativeSource = normativeSource;
        this.source = source;
        this.versions = versions;
    }

    public String getNormativeSource() {
        return normativeSource;
    }

    public String getSource() {
        return source;
    }

    public List<String> getVersions() {
        return versions;
    }
}
