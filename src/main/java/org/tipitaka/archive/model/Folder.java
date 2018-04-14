package org.tipitaka.archive.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by christian on 03.09.16.
 */
public class Folder extends ExtendedBase {

    private final List<Menu> menus;
    private final List<String> versions;

    public Folder(String path, Script script, String version, String baseUrl, List<String> titlePath, List<String> versions, Menu... menus) {
        this(path, script, version, baseUrl, titlePath, versions, Arrays.asList(menus));
    }

    public Folder(String path, Script script, String version, String baseUrl, List<String> titlePath, List<String> versions, List<Menu> menus) {
        super(path, script, version, baseUrl, titlePath);
        this.menus = menus;
        this.versions = versions;
    }

    public List<Menu> getMenus() {
        return menus;
    }

    public List<String> getVersions() {
        return versions;
    }
}
