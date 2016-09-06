package org.tipitaka.archive.model;

import java.io.IOException;

/**
 * Created by christian on 06.09.16.
 */
class JsonVisitor implements Visitor {

    private final Appendable output;
    private boolean needsComma = false;

    JsonVisitor(Appendable output){
        this.output = output;
    }

    private JsonVisitor append(String s) throws IOException {
        if (needsComma) {
            needsComma = false;
            output.append(",");
        }
        output.append(s);
        return this;
    }

    @Override
    public void visitStartObject() throws IOException {
        append("{");
    }

    @Override
    public void visitEndObject() throws IOException {
        needsComma = false;
        append("}");
        needsComma = true;
    }

    @Override
    public void visitStartArray() throws IOException {
        append("[");
    }

    @Override
    public void visitEndArray() throws IOException {
        needsComma = false;
        append("]");
        needsComma = true;
    }

    @Override
    public void visitField(String name) throws IOException {
        append("\"").append(name).append("\":");
    }

    @Override
    public void visitStringValue(String value) throws IOException {
        append("\"").append(value).append("\"");
        needsComma = true;
    }

    @Override
    public void visitValue(String value) throws IOException {
        append(value);
        needsComma = true;
    }
}
