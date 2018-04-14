package org.tipitaka.archive.notes;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by cmeier on 3/12/16.
 */
public class Notes
{

  @JacksonXmlProperty(isAttribute = true, localName = "archive-path")
  public String archivePath;

  @JsonIgnore
  Map<Integer, List<Note>> perLine = new LinkedHashMap<>();

  public List<Note> notes = new LinkedList<>();

  @JsonIgnore
  private Set<Version> versions = new HashSet<>();

  @JacksonXmlProperty(localName = "note")
  public void addNote(Note note) {
    if (note.id != this.notes.size()) {
      throw new IllegalArgumentException("mismatched id: " + note + " expected " + notes.size());
    }
    this.notes.add(note);
    if (note.alternatives != null) {
      for (Alternative alt: note.alternatives) {
        versions.add(alt.getVersion());
      }
    }
    List<Note> notes = perLine.get(note.referenceLine);
    if (notes == null) {
      notes = new LinkedList<>();
      perLine.put(note.referenceLine, notes);
    }
    notes.add(note);
  }

  public List<Version> getVersions(){
    return new LinkedList(versions);
  }

  public Note get(final int id) {
    return id < notes.size() ? notes.get(id): null;
  }

  public boolean isEmpty() {
    return this.notes.isEmpty();
  }

  Note findNote(String referenceLine) {
    List<Note> notes = perLine.get(referenceLine);
    if (notes != null && notes.size() == 1) {
      return notes.get(0);
    }
    return null;
  }

  List<Note> findNotes(String referenceLine) {
    List<Note> notes = perLine.get(referenceLine);
    if (notes == null) {
      return Collections.emptyList();
    }
    return notes;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Notes { ");
    sb.append("archivePath='").append(archivePath).append('\'');
    sb.append("\n  ");
    for (Note note : notes) {
      sb.append(note).append("\n  ");
    }
    sb.append('}');
    return sb.toString();
  }
}
