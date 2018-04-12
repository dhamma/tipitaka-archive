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
import java.util.stream.Collectors;
import org.tipitaka.archive.model.*;

import org.tipitaka.archive.notes.Notes;
import org.tipitaka.archive.notes.NotesLocator;

import org.tipitaka.archive.StandardException;

public class ModelBuilder {
  interface Action<T> {
    void act(T menu) throws IOException;
  }

    private static final String BASE_URL = "file:.";

    private final DirectoryStructure directory;
    private final Map<String, Menu> menus;

    public ModelBuilder() throws IOException, StandardException {
        this(TipitakaOrgTocVisitor.mirror());
    }

    public ModelBuilder(TipitakaOrgTocVisitor visitortocVisitor) throws IOException, StandardException {
        this.directory = new DirectoryStructure(visitortocVisitor);
        this.menus = new HashMap<String, Menu>();
    }

    public void eachMenu(Action<Menu> action) throws IOException {
        for (Node node: directory.nodes) {
            buildMenus(node);
        }
        for (Menu menu: menus.values()) {
            action.act(menu);
        }
    }

    public void eachFolder(Action<Folder> action) throws IOException {
        visitFolders(action, directory.root);
    }

    public void eachDocument(Action<Document> action) throws IOException {
        for (Node node: directory.nodes) {
            Document doc = buildDocument(node);
            if (doc != null) action.act(doc);
        }
    }

    private void visitFolders(Action<Folder> action, Node node) throws IOException {
        Folder folder = buildFolder(node);
        if (folder != null) action.act(folder);
        if (node.children != null) {
            for (Node child: node.children.values()) {
                visitFolders(action, child);
            }
        }
    }

    private Folder buildFolder(Node node) {
        if (node.normativePath() == null) {
            return new Folder(node.path(), Script.roman, null, BASE_URL, node.titlePath(), buildMenus(node));
        }
        else {
            return null;
        }
    }

    public Document buildDocument(Node node) throws IOException {
      if (node.normativePath() == null) {
        return null;
      }
      else {
        Notes notes = NotesLocator.toNotes(node.path());
        if (!notes.getVersions().isEmpty()) System.out.println(node.normativePath() + " " + notes.getVersions());
        return new Document(node.path(), Script.roman, null, BASE_URL, node.titlePath(), node.normativePath(), "/roman" + node.path() + ".xml", Collections.emptyList(), buildMenus(node.parent));
      }
    }

    private List<Menu> buildMenus(Node node) {
        LinkedList<Menu> menus = new LinkedList<Menu>();
        addMenu(node, menus);
        return menus;
    }

    private void addMenu(Node node, LinkedList<Menu> menus) {
        Menu menu = getOrBuild(node);
        menus.addFirst(menu);
        if (node.parent != null) {
            addMenu(node.parent, menus);
        }
    }

    private Menu getOrBuild(Node node) {
        Menu menu;
        String path = node.path();
        if (this.menus.containsKey(path)) {
            menu = this.menus.get(path);
        }
        else {
            menu = buildMenu(node);
            this.menus.put(path, menu);
        }
        return menu;
    }

    private Menu buildMenu(Node node) {
        if (node.children == null) return null;
        Map<String, String> items = new LinkedHashMap<String, String>();
        for(Map.Entry<String, Node> entry: node.children.entrySet()) {
            items.put(entry.getValue().pathName(),
                      entry.getValue().titlePath().get(0));
        }
        return new Menu(node.path(), items);
    }
}
