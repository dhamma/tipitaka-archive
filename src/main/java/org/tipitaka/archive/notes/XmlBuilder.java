package org.tipitaka.archive.notes;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.tipitaka.archive.notes.Note;
import org.tipitaka.archive.notes.Notes;

/**
 * Created by christian on 03.09.16.
 */
public class XmlBuilder {

    interface Action {
        void act() throws IOException;
    }

    private final Appendable appendable;

    private final String spaces;

    private String indent = "";

    public XmlBuilder(Appendable appendable) {
        this(appendable, "  ");
    }

    public XmlBuilder(Appendable appendable, String spaces) {
        this.appendable = appendable;
        this.spaces = spaces;
    }

    private Appendable append(CharSequence obj) throws IOException {
        return appendable.append(indent).append(obj);
    }

    private void newLine() throws IOException {
        if (spaces.length() > 0) {
            appendable.append("\n");
        }
    }

    public void tag(String name, String value) {
        tag(name, value, Collections.emptyMap());
    }

    public void tag(String name, String value, Map<String, String> attributes) {
        if (value == null) return;
        try {
            append("<").append(name);
            appendAttributes(attributes);
            appendable.append(">")
                .append(value)
                .append("</").append(name).append(">");
            newLine();
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void nestedTag(String name, Action action) throws IOException {
        nestedTag(name, Collections.emptyMap(), action);
    }

    public void nestedTag(String name, Map<String, String> attributes, Action action) {
        try {
            append("<").append(name);
            appendAttributes(attributes);
            appendable.append(">");
            newLine();
            indent += spaces;
            action.act();
            indent = indent.substring(0, indent.length() - spaces.length());
            append("</").append(name).append(">");
            newLine();
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void appendAttributes(Map<String, String> attributes) throws IOException {
        for(Map.Entry<String, String> map: attributes.entrySet()) {
            appendable.append(" ")
                .append(map.getKey())
                .append("=\"")
                .append(map.getValue())
                .append("\"");
        }
    }

    public void build(Notes notes) throws IOException {
        try {
            Map<String, String> attributes = Collections.singletonMap("archive-path", notes.archivePath);

            nestedTag("notes", attributes,
                      () -> notes.notes.forEach(note -> build(note)));
        }
        catch(RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            else {
                throw e;
            }
        }
    }

    private void build(Note note) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("id", Integer.toString(note.id));
        attributes.put("reference-line", Integer.toString(note.referenceLine));
        nestedTag("note", attributes, () -> note(note));
    }

    private void note(Note note) throws IOException {
        tag("original", note.original);
        tag("type", note.type.name());
        tag("snippet", note.snippet);
        tag("hint", note.hint);
        tag("match", note.match);
        if (!note.alternatives.isEmpty()) {
          nestedTag("alternatives",
                    () -> note.alternatives.forEach(alt -> build(alt)));
        }
        if (note.references != null) {
            nestedTag("references",
                      () -> note.references.forEach(ref -> tag("reference", ref)));
        }
    }

    private void build(Alternative alternative) {
        Map<String, String> attributes = new HashMap<>();
        if (alternative.sourceAbbreviation != null) {
            attributes.put("source-abbr", alternative.sourceAbbreviation);
        }
        attributes.put("source", alternative.source);
        tag("alternative", alternative.text, attributes);
    }
}
