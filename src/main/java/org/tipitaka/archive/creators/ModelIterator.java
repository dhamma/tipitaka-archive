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
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.tipitaka.archive.model.*;

import org.tipitaka.archive.notes.Notes;
import org.tipitaka.archive.notes.NotesLocator;
import org.tipitaka.archive.notes.Version;

import org.tipitaka.archive.StandardException;

public class ModelIterator {
  interface Action<T> {
    void act(T menu) throws IOException;
  }

  private static final String BASE_URL = "file:.";

  private final DirectoryStructure directory;
  private final Map<String, Menu> menus;
  private final Map<String, Set<String>> versions;

  public ModelIterator() throws IOException, StandardException {
    this(TipitakaOrgTocVisitor.mirror());
  }

  public ModelIterator(final TipitakaOrgTocVisitor visitortocVisitor) throws IOException, StandardException {
    this.directory = new DirectoryStructure(visitortocVisitor);
    this.menus = new HashMap<String, Menu>();
    this.versions = new HashMap<String, Set<String>>();
    collectVersions();
  }

  private void collectVersions() throws IOException {
    for (Node node: this.directory.nodes) {
      if (node.normativePath() != null) {
        String path = node.path();
        Set<String> versions = new TreeSet(getVersions(node));
        this.versions.put(path, versions);
        addVersions(path.replaceFirst("/[^/]+$", ""), versions);
      }
    }
  }

  private void addVersions(final String path, Set<String> versions) {
    this.versions.putIfAbsent(path, new TreeSet<String>());
    this.versions.get(path).addAll(versions);
    if (path.length() > 0) {
      addVersions(path.replaceFirst("/[^/]+$", ""), versions);
    }
  }

  public void eachMenu(final Action<Menu> action) throws IOException {
    for (Node node: directory.nodes) {
      buildMenus(node);
    }
    for (Menu menu: menus.values()) {
      action.act(menu);
    }
  }

  public void eachFolder(final Action<Folder> action) throws IOException {
    visitFolders(action, directory.root);
  }

  public void eachDocument(final Action<Document> action) throws IOException {
    for (Node node: directory.nodes) {
      Document doc = buildDocument(node);
      if (doc != null) action.act(doc);
    }
  }

  private void visitFolders(final Action<Folder> action, final Node node) throws IOException {
    Folder folder = buildFolder(node);

    if (folder != null) action.act(folder);
    if (node.children != null) {
      for (Node child: node.children.values()) {
        visitFolders(action, child);
      }
    }
  }

  private Folder buildFolder(final Node node) {
    if (node.normativePath() == null) {
      return new Folder(node.path(), Script.roman, null, BASE_URL, node.titlePath(), new LinkedList<String>(this.versions.get(node.path())), buildMenus(node));
    }
    else {
      return null;
    }
  }

  private Document buildDocument(final Node node) throws IOException {
    if (node.normativePath() == null) {
      return null;
    }
    else {
      return new Document(node.path(), Script.roman, null, BASE_URL, node.titlePath(), node.normativePath(), "/roman" + node.path() + ".xml", getVersions(node), buildMenus(node.parent));
    }
  }

  private List<String> getVersions(final Node node) throws IOException {
      final Notes notes = NotesLocator.toNotes(node.path());
      final List<String> versions = notes.getVersions().stream().map(v -> v.name()).collect(Collectors.toList());
      versions.add(Version.VIPASSANA_RESEARCH_INSTITUT.name());
      return versions;
  }

  private List<Menu> buildMenus(final Node node) {
    LinkedList<Menu> menus = new LinkedList<Menu>();
    addMenu(node, menus);
    return menus;
  }

  private void addMenu(final Node node, final LinkedList<Menu> menus) {
    Menu menu = getOrBuild(node);
    menus.addFirst(menu);
    if (node.parent != null) {
      addMenu(node.parent, menus);
    }
  }

  private Menu getOrBuild(final Node node) {
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

  private Menu buildMenu(final Node node) {
    if (node.children == null) return null;
    Map<String, String> items = new LinkedHashMap<String, String>();
    for(Map.Entry<String, Node> entry: node.children.entrySet()) {
      items.put(entry.getValue().pathName(),
                entry.getValue().titlePath().get(0));
    }
    return new Menu(node.path(), items);
  }
}
