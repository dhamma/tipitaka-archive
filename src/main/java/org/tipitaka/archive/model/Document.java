package org.tipitaka.archive.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;
import java.util.List;

/**
 * Created by christian on 03.09.16.
 */
public class Document extends Folder {
    private final String normativeSource;
    private final String source;
    private final List<String> versions;

    @JsonCreator
    public Document(String path, Script script, String version,
                    String baseUrl, List<String> titlePath,
                    String normativeSource, String source,
                    List<String> versions, Menu... menus) {
        this(path, script, version, baseUrl, titlePath, normativeSource,
             source, versions, Arrays.asList(menus));
    }

    public Document(String path, Script script, String version,
                    String baseUrl, List<String> titlePath,
                    String normativeSource, String source,
                    List<String> versions, List<Menu> menus) {
        super(path, script, version, baseUrl, titlePath, menus);
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
