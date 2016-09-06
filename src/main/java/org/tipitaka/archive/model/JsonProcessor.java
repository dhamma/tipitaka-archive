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

    private final List<String> skip;
    private final String file;

    private boolean silence = false;

    JsonProcessor(String file, String... skip) throws IOException {
        this.skip = Arrays.asList(skip);
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
                    if (!silence) visitField(name, visitor, parser);
                    break;
                case VALUE_STRING:
                    if (!silence) visitor.visitStringValue(parser.getValueAsString());
                    break;
                case VALUE_FALSE:
                case VALUE_NULL:
                case VALUE_TRUE:
                case VALUE_NUMBER_FLOAT:
                case VALUE_NUMBER_INT:
                    if (!silence) visitor.visitValue(parser.getValueAsString());
                    break;
                default:
                    throw new RuntimeException("TODO " +token);
            }
            token = parser.nextToken();
        }
    }

    private void visitField(String name, Visitor visitor, JsonParser parser) throws IOException {
        boolean skipped = skip.indexOf(name) > -1 && ! silence;
        if (skipped){
            silence = true;
        }
        if (!silence) visitor.visitField(name);
        JsonToken token = parser.nextToken();
        switch(token) {
            case START_OBJECT:
                visitObject(visitor, parser);
                break;
            case START_ARRAY:
                visitArray(visitor, parser);
                break;
            case VALUE_STRING:
                if (!silence) visitor.visitStringValue(parser.getValueAsString());
                break;
            default:
                if (!silence) visitor.visitValue(parser.getValueAsString());
        }
        if (skipped) silence = false;
    }

    private void visitObject(Visitor visitor, JsonParser parser) throws IOException {
        if (!silence) visitor.visitStartObject();
        dispatch(visitor, parser, JsonToken.END_OBJECT);
        if (!silence) visitor.visitEndObject();
    }

    private void visitArray(Visitor visitor, JsonParser parser) throws IOException {
        if (!silence) visitor.visitStartArray();
        dispatch(visitor, parser, JsonToken.END_ARRAY);
        if (!silence) visitor.visitEndArray();
    }
}
