package org.tipitaka.archive.model;

import java.io.IOException;
import java.util.Map;

/**
 * Created by christian on 03.09.16.
 */
public class JsonApiBuilder {

    private final Appendable appendable;

    private final String spaces;

    private String indent = "";

    public JsonApiBuilder(Appendable appendable) {
        this(appendable, "  ");
    }

    public JsonApiBuilder(Appendable appendable, String spaces) {
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

    public void build(Menu menu) throws IOException {
        buildRaw(menu);
        dedentHash();
    }
    public void buildRaw(Menu menu) throws IOException {
        indentHash();
        buildType(menu);
        buildId(menu);
        comma();
        buildBaseAttributes(menu);
        buildHasDocuments(menu.hasDocuments());
        buildItems(menu.getItems());
        dedent("},");
        buildLinks(menu);
    }

    public void build(ToC toc) throws IOException {
        indentHash();
        append("\"data\":");
        indentHash();
        buildType(toc);
        buildId(toc);
        comma();
        buildExtendedBaseAttributes(toc);
        buildHasDocuments(toc.hasDocuments());
        buildItems(toc.getItems());
        dedent("},");
        buildLinks(toc);
        dedent("},");
        append("\"relationships\":");
        indentHash();
        append("\"menus\":");
        indentArray();
        int index = 0;
        for(Menu menu: toc.getMenus()) {
            append("");
            indentHash();
            buildType(menu);
            buildId(menu);
            newLine();
            if (++index < toc.getMenus().size()) {
                dedent("},");
            }
            else {
                dedentHash();;
            }
        }
        dedentArray();
        dedent("},");
        append("\"included\":");
        indentArray();
        index = 0;
        for(Menu menu: toc.getMenus()) {
            append("");
            buildRaw(menu);
            if (++index < toc.getMenus().size()) {
                dedent("},");
            }
            else {
                dedentHash();;
            }
        }
        dedentArray();
        dedentHash();
    }

    public void build(Document document) throws IOException {
        indentHash();
        append("\"data\":");
        indentHash();
        buildType(document);
        buildId(document);
        comma();
        buildExtendedBaseAttributes(document);
        append("\"normativeSource\":\"").append(document.getNormativeSource()).append("\"");
        comma();
        append("\"source\":\"").append(document.getSource()).append("\"");
        newLine();
        dedent("},");
        buildLinks(document);
        dedent("},");
        append("\"relationships\":");
        indentHash();
        append("\"menus\":");
        indentArray();
        int index = 0;
        for(Menu menu: document.getMenus()) {
            append("");
            indentHash();
            buildType(menu);
            buildId(menu);
            newLine();
            if (++index < document.getMenus().size()) {
                dedent("},");
            }
            else {
                dedentHash();;
            }
        }
        dedentArray();
        dedent("},");
        append("\"included\":");
        indentArray();
        index = 0;
        for(Menu menu: document.getMenus()) {
            append("");
            buildRaw(menu);
            if (++index < document.getMenus().size()) {
                dedent("},");
            }
            else {
                dedentHash();;
            }
        }
        dedentArray();
        dedentHash();
    }


    private void buildHasDocuments(Boolean hasDocuments) throws IOException {
        append("\"hasDocuments\":").append(hasDocuments.toString());
        comma();
    }

    private void buildLinks(Base base) throws IOException {
        append("\"links\":");
        indentHash();
        append("\"self\":\"");
        buildRawId(base);
        appendVersion(base);
        appendable.append("\"");
        comma();
        append("\"xml\":\"");
        buildRawId(base);
        appendable.append(".xml");
        appendVersion(base);
        appendable.append("\"");
        comma();
        append("\"html\":\"");
        buildRawId(base);
        appendable.append(".html");
        appendVersion(base);
        appendable.append("\"");
        newLine();
        dedentHash();
    }

    private void appendVersion(Base base) throws IOException {
        if (base.getVersion() != null) {
            appendable.append("?version=").append(base.getVersion());
        }
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

    private void buildExtendedBaseAttributes(ExtendedBase base) throws IOException {
        buildBaseAttributes(base);
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

    private void buildBaseAttributes(Base base) throws IOException {
        append("\"attributes\":");
        indentHash();
        append("\"script\":\"").append(base.getScript().name()).append("\"");
        comma();
        append("\"path\":");
        if (base.getPath() == null) {
            appendable.append("null");
        } else {
            appendable.append("\"").append(base.getPath()).append("\"");
        }
        comma();
        append("\"version\":");
        if (base.getVersion() == null) {
            appendable.append("null");
        } else {
            appendable.append("\"").append(base.getVersion()).append("\"");
        }
        comma();

    }

    private void buildRawId(Base base) throws IOException {
        appendable.append("/").append(base.getScript().name());
        if (base.getPath() != null) {
            appendable.append(base.getPath());
        }
    }

    private void buildId(Base base) throws IOException {
        append("\"id\":\"");
        buildRawId(base);
        appendable.append("\"");
    }

    private void buildType(Base base) throws IOException {
        append("\"type\":\"").append(base.getClass().getSimpleName().toLowerCase())
                .append("\"");
        comma();
    }

    private void comma() throws IOException {
        appendable.append(",");
        newLine();
    }
}
