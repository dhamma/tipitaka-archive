package org.tipitaka.archive.model;

import java.io.IOException;
import java.util.Stack;

/**
 * Created by christian on 06.09.16.
 */
class XmlVisitor implements Visitor {

    private final Appendable output;
    private final Stack<String> stack = new Stack<>();
    private String listName;
    private String arrayName;

    XmlVisitor(Appendable output) throws IOException {
        this.output = output;
        output.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        visitField("tipitaka");
    }

    private XmlVisitor append(String s) throws IOException {
        output.append(s);
        return this;
    }

    @Override
    public void visitStartObject() throws IOException {
        if (arrayName != null && stack.peek().startsWith(arrayName)) {
            visitField(arrayName);
        }
    }

    @Override
    public void visitEndObject() throws IOException {
        if ("items".equals(stack.peek())) {
            listName = null;
        }
        append("</").append(stack.pop()).append(">");
    }

    @Override
    public void visitStartArray() throws IOException {
        if ("titlePath".equals(stack.peek())) {
            listName = "item";
        }
        if ("versions".equals(stack.peek())) {
            listName = "version";
        }
        if ("menus".equals(stack.peek())) {
            arrayName = "menu";
        }
    }

    @Override
    public void visitEndArray() throws IOException {
        if (arrayName != null) {
            append("</").append(stack.pop()).append(">");
        }
        else {
            append("</").append(stack.pop()).append(">");
        }
        listName = null;
        arrayName = null;
    }

    @Override
    public void visitField(String name) throws IOException {
        if (listName != null) {
            append("<item><key>").append(name).append("</key>");
        }
        else {
            if ("items".equals(name)) {
                listName = "value";
            }
            append("<").append(name).append(">");
            stack.push(name);
        }
    }

    @Override
    public void visitStringValue(String value) throws IOException {
        if (listName != null) {
            append("<").append(listName).append(">")
                    .append(value)
                    .append("</").append(listName).append(">");
            String name = stack.peek();
            if (!name.equals("titlePath") && !name.equals("versions")) {
                append("</item>");
            }
        } else {
            append(value).append("</").append(stack.pop()).append(">");
        }
    }

    @Override
    public void visitValue(String value) throws IOException {
       visitStringValue(value);
    }
}
