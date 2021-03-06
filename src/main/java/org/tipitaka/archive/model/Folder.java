package org.tipitaka.archive.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by christian on 03.09.16.
 */
public class Folder extends ExtendedBase {

    private final List<Menu> menus;

    public Folder(String path, Script script, String version, String baseUrl, List<String> titlePath, Menu... menus) {
        this(path, script, version, baseUrl, titlePath, Arrays.asList(menus));
    }

    public Folder(String path, Script script, String version, String baseUrl, List<String> titlePath, List<Menu> menus) {
        super(path, script, version, baseUrl, titlePath);
        this.menus = menus;
    }

    public List<Menu> getMenus() {
        return menus;
    }
}
