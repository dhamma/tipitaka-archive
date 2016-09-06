package org.tipitaka.archive.model;

import java.io.IOException;

/**
 * Created by christian on 06.09.16.
 */
interface Visitor {
    void visitStartObject() throws IOException;

    void visitEndObject() throws IOException;

    void visitStartArray() throws IOException;

    void visitEndArray() throws IOException;

    void visitField(String name) throws IOException;

    void visitStringValue(String value) throws IOException;

    void visitValue(String value) throws IOException;
}
