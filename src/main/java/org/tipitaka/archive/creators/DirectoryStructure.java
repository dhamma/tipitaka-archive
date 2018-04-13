package org.tipitaka.archive.creators;

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

import org.tipitaka.archive.StandardException;

import org.xmlpull.v1.XmlPullParserException;

public class DirectoryStructure {

    private static final Set<String> EMPTY = Collections.emptySet();

    public final Node root = new Node();
    public final List<Node> nodes = new LinkedList<Node>();
    private final Map<String, String> map = new LinkedHashMap<String, String>();
    private final Map<String, String> rmap = new HashMap<String, String>();

    public DirectoryStructure() throws IOException, StandardException {
        this(TipitakaOrgTocVisitor.mirror());
    }

    public DirectoryStructure(TipitakaOrgTocVisitor visitor) throws IOException, StandardException {
        try {
            visitor.accept();
        }
        catch (XmlPullParserException e) {
            throw new StandardException("can not parse tipitaka.org ToC", e);
        }
        nodes.clear();
        nodes.add(root);
        List<String> parts = new ArrayList<String>(6);
        for(Map.Entry<String, String[]> entry: visitor.map().entrySet()){
            parts.clear();
            for(String part: entry.getValue()){
                if(part != null){
                    parts.add(part);
                }
            }
            Node node = root.addLeaf(entry.getKey(), parts);
            nodes.add(node);
        }
    }
}
