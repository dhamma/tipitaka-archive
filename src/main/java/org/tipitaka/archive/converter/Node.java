package org.tipitaka.archive.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Node {

    Map<String, Node> children;
    Node parent;
    String name;
    String file;

    Node(String name, Node parent, boolean leaf){
        this.name = name;
        this.parent = parent;
        this.children = leaf ? null : new LinkedHashMap<String, Node>();
    }

    Node(String name, Node parent){
        this(name, parent, false);
    }

    Node() {
        this("", null, false);
    }

    Node addLeaf(String key, List<String> parts) {
        StringBuilder result = new StringBuilder();
        Node n = this;
        for(String part: parts){
            result.append("/").append(part);
            Node node = n.get(part);
            while (node == null){
                node = n.addChild(part);
            }
            n = node;
        }
        if (n.file != null) {
            n = n.parent.addChild(n.name + " _2_");
        }
        n.children = null;
        n.file = key;
        return n;
    }

    Node addChild(String name){
        int i = 2;
        while (children.containsKey(name)) {
            name = name.replace("_" + i + "_", "_" + (++i) + "_");
        }
        Node n = new Node(name, this);
        children.put(name, n);
        return n;
    }

    Node addLeaf(String leaf, String value){
        Node n = new Node(value, this, true);
        children.put(leaf, n);
        return n;
    }

    Node get(String name){
        return children.get(name);
    }

    Node getNode(String... parts){
        Node n = this;
        for(String part: parts){
            n = n.get(part);
        }
        return n;
    }

    String pathName() {
        return RomanScriptHelper.removeDiacritcals(name.replaceFirst("\\.$", "").trim()).toLowerCase();
    }

    public String path() {
        StringBuilder result = new StringBuilder();
        path(result);
        return result.toString();
    }

    public String normativePath() {
        return file;
    }

    public List<String> titlePath() {
        List<String> result = new LinkedList<String>();
        titlePath(result);
        return result;
    }

    void titlePath(List<String> result) {
        if (parent != null) {
            result.add(name);
            parent.titlePath(result);
        }
    }

    public void path(StringBuilder result) {
        if (parent != null) {
            result.insert(0, pathName()).insert(0, '/');
            parent.path(result);
        }
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        path(result);
        result.append(" ");
        result.append(titlePath().toString());
        if (file != null) {
            result.append(" ").append(file);
        }
        return result.toString();
    }
}
