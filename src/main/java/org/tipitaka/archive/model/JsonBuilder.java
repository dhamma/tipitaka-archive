package org.tipitaka.archive.model;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by christian on 03.09.16.
 */
public class JsonBuilder {

    private final Appendable appendable;

    private final String spaces;

    private String indent = "";

    public JsonBuilder(Appendable appendable) {
        this(appendable, "  ");
    }

    public JsonBuilder(Appendable appendable, String spaces) {
        this.appendable = appendable;
        this.spaces = spaces;
    }

    private Appendable append(CharSequence obj) throws IOException {
        return appendable.append(indent).append(obj);
    }

    private void indentHash() throws IOException {
        indent("{");
    }

    private void indentArray() throws IOException {
        indent("[");
    }

    private void indent(String ch) throws IOException {
        appendable.append(ch);
        newLine();
        indent = indent + spaces;
    }

    private void newLine() throws IOException {
        if (spaces.length() > 0) {
            appendable.append("\n");
        }
    }

    private void dedentHash() throws IOException {
        dedent("}");
    }

    private void dedentArray() throws IOException {
        dedent("]");
    }

    private void dedent(String ch) throws IOException {
        indent = indent.substring(0, indent.length() - spaces.length());
        append(ch);
        newLine();
    }

    private void comma() throws IOException {
        appendable.append(",");
        newLine();
    }

    public void build(Folder folder) throws IOException {
        indentHash();
        buildExtendedBase(folder);
        buildVersions(folder);
        buildMenus(folder.getMenus());
        dedentHash();
    }

    public void build(Document document) throws IOException {
        indentHash();
        buildExtendedBase(document);
        append("\"normativeSource\":\"").append(document.getNormativeSource()).append("\"");
        comma();
        append("\"source\":\"").append(document.getSource()).append("\"");
        comma();
        buildVersions(document);
        buildMenus(document.getMenus());
        dedentHash();
    }

    private void buildVersions(Folder folder)  throws IOException {
        append("\"versions\":[");
        int index = 0;
        for(String version: folder.getVersions()) {
            appendable.append("\"").append(version).append("\"");
            if (++index < folder.getVersions().size()) {
                appendable.append(",");
            }
        }
        appendable.append("]");
        comma();
    }

    private void buildItems(Map<String,String> items) throws IOException {
        append("\"items\":");
        indentHash();
        int index = 0;
        for (Map.Entry<String, String> entry: items.entrySet()) {
            append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
            if (++index < items.size()) comma();
            else newLine();
        }
        dedentHash();
    }

    private void buildExtendedBase(ExtendedBase base) throws IOException {
        buildBase(base);
        append("\"script\":\"").append(base.getScript().name()).append("\"");
        comma();
        append("\"version\":");
        if (base.getVersion() == null) {
            appendable.append("null");
        }
        else {
            appendable.append("\"").append(base.getVersion()).append("\"");
        }
        comma();
        append("\"baseUrl\":\"").append(base.getBaseUrl()).append("\"");
        comma();
        append("\"titlePath\":");
        indentArray();
        int index = 0;
        for(String part: base.getTitlePath()) {
            append("\"").append(part).append("\"");
            if (++index < base.getTitlePath().size()) {
                appendable.append(",");
            }
            newLine();
        }
        dedent("],");
    }

    private void buildMenus(List<Menu> menus) throws IOException {
        append("\"menus\":");
        indentArray();
        int index = 0;
        for(Menu menu: menus) {
            appendable.append(indent);
            indentHash();
            buildBase(menu);
            buildItems(menu.getItems());
            if (++index < menus.size()) {
                dedent("},");
            }
            else {
                dedentHash();
            }
        }
        dedentArray();
    }

    private void buildBase(Base base) throws IOException {
        append("\"path\":");
        if (base.getPath() == null) {
            appendable.append("null");
        }
        else {
            appendable.append("\"").append(base.getPath()).append("\"");
        }
        comma();
    }
}
