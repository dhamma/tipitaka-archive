package org.tipitaka.archive.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by christian on 03.09.16.
 */
class ExtendedBase extends Base {

    protected final List<Menu> menus;
    private final List<String> titlePath;

    ExtendedBase(Script script, String path, String version, List<String> titlePath, Menu... menus) {
        super(script, path, version);
        this.titlePath = titlePath;
        this.menus = Arrays.asList(menus);
    }

    public List<String> getTitlePath() {
        return titlePath;
    }

    public List<Menu> getMenus() {
        return menus;
    }
}
