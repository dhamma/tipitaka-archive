package org.tipitaka.archive.model;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by christian on 06.09.16.
 */
class JsonProcessor {


    private static final JsonFactory factory = new JsonFactory();

    private final String file;

    JsonProcessor(String file) throws IOException {
        this.file = file;
    }

    void accept(Visitor visitor) throws IOException {
        JsonParser parser = factory.createParser(new FileReader(file));
        dispatch(visitor, parser, null);
    }

    private void dispatch(Visitor visitor, JsonParser parser, JsonToken end) throws IOException {
        JsonToken token = parser.nextToken();
        while(token != end) {
            switch(token) {
                case START_OBJECT:
                    visitObject(visitor, parser);
                    break;
                case START_ARRAY:
                    visitArray(visitor, parser);
                    break;
                case FIELD_NAME:
                    String name = parser.getValueAsString();
                    visitField(name, visitor, parser);
                    break;
                case VALUE_STRING:
                    System.out.println("->" + parser.getValueAsString());
                    visitor.visitStringValue(parser.getValueAsString());
                    break;
                case VALUE_FALSE:
                case VALUE_NULL:
                case VALUE_TRUE:
                case VALUE_NUMBER_FLOAT:
                case VALUE_NUMBER_INT:
                    visitor.visitValue(parser.getValueAsString());
                    break;
                default:
                    throw new RuntimeException("TODO " +token);
            }
            token = parser.nextToken();
        }
    }

    private void visitField(String name, Visitor visitor, JsonParser parser) throws IOException {
        System.out.println("=>" + name);
        visitor.visitField(name);
        JsonToken token = parser.nextToken();
        switch(token) {
            case START_OBJECT:
                visitObject(visitor, parser);
                break;
            case START_ARRAY:
                visitArray(visitor, parser);
                break;
            case VALUE_STRING:
                visitor.visitStringValue(parser.getValueAsString());
                break;
            default:
                visitor.visitValue(parser.getValueAsString());
        }
    }

    private void visitObject(Visitor visitor, JsonParser parser) throws IOException {
        visitor.visitStartObject();
        dispatch(visitor, parser, JsonToken.END_OBJECT);
        visitor.visitEndObject();
    }

    private void visitArray(Visitor visitor, JsonParser parser) throws IOException {
        visitor.visitStartArray();
        dispatch(visitor, parser, JsonToken.END_ARRAY);
        visitor.visitEndArray();
    }
}
