package org.tipitaka.archive.notes;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

/**
 * Created by cmeier on 3/12/16.
 */
public class Notes
{

  @JacksonXmlProperty(isAttribute = true, localName = "archive-path")
  public String archivePath;

  Map<String, List<Note>> perLine = new LinkedHashMap<>();
  List<Note> notes = new LinkedList<>();
  Map<String, String> versions = new LinkedHashMap<>();

  @JacksonXmlProperty(localName = "note")
  public void addNote(Note note) {
    if (note.id != this.notes.size()) {
      throw new IllegalArgumentException("mismatched id: " + note + " expected " + notes.size());
    }
    this.notes.add(note);
    if (note.alternatives != null) {
      for (Alternative alt: note.alternatives) {
        versions.putIfAbsent(alt.sourceAbbreviation, alt.source);
      }
    }
    List<Note> notes = perLine.get(note.referenceLine);
    if (notes == null) {
      notes = new LinkedList<>();
      perLine.put(note.referenceLine, notes);
    }
    notes.add(note);
  }

  public Map<String, String> getVersions(){
    return Collections.unmodifiableMap(versions);
  }

  public Note get(final int id) {
    return id < notes.size() ? notes.get(id): null;
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
    sb.append("\n");
    for (Note note : notes) {
      sb.append(note).append("\n");
    }
    sb.append('}');
    return sb.toString();
  }
}
