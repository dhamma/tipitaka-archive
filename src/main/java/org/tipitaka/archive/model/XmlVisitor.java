package org.tipitaka.archive.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Stack;

/**
 * Created by christian on 06.09.16.
 */
class XmlVisitor implements Visitor {

    private final File basedir;
    private final Appendable output;
    private final Stack<String> stack = new Stack<>();
    private String listName;
    private String arrayName;
    private boolean isSource = false;
    private String source = null;

    XmlVisitor(File basedir, Appendable output) throws IOException {
        this.basedir = basedir;
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
        String tag = stack.pop();
        if ("tipitaka".equals(tag) && source != null) {
            embedSourceDocument();
        }
        append("</").append(tag).append(">");
    }

    private void embedSourceDocument() throws IOException {
        // we want to stream if possible
        try(BufferedReader in = new BufferedReader(new FileReader(new File(basedir, this.source)))) {
            String line = in.readLine();
            while(line != null) {
                append(line);
                line = in.readLine();
            }
        }
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
            this.isSource = "source".equals(name);
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
            if (this.isSource) this.source = value;
            append(value).append("</").append(stack.pop()).append(">");
        }
    }

    @Override
    public void visitValue(String value) throws IOException {
       visitStringValue(value);
    }
}
