package org.tipitaka.archive;

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

  enum Type { raw, auto, manual, no_match }

  enum Version {
    SRI_LANKA("sī."), PALI_TEXT_SOCIETY("pī."), VIPASSANA_RESEARCH_INSTITUT("vri"),
    CAMBODIAN("ka."), THAI("syā."), UNKNOWN("?");

    private final String abbrevation;

    Version(String abbreviation) {
      this.abbrevation = abbreviation;
    }

    String getAbbrevation() {
      return this.abbrevation;
    }

    String getName() {
      return name().toLowerCase().replace("_", " ");
    }

    static Version toVersion(String name) {
      for (Version version: values()) {
        if (version.getAbbrevation().equals(name)) {
          return version;
        }
      }
      return null;
    }
  }

  public static class Note
  {

    @JacksonXmlProperty(isAttribute = true)
    public int id;

    @JacksonXmlProperty(isAttribute = true)
    public String line;

    @JacksonXmlProperty(isAttribute = true, localName = "reference-line")
    public String referenceLine;

    @JacksonXmlProperty(isAttribute = true)
    public String original;

    public Type type;

    public List<Alternative> alternatives;

    public String previous;

    String getKey() {
      return referenceLine + " " + original;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof Note)) {
        return false;
      }

      Note note = (Note) o;

      if (id != note.id) {
        return false;
      }
      if (!line.equals(note.line)) {
        return false;
      }
      if (!referenceLine.equals(note.referenceLine)) {
        return false;
      }
      if (!original.equals(note.original)) {
        return false;
      }
      if (!type.equals(note.type)) {
        return false;
      }
      if (!alternatives.equals(note.alternatives)) {
        return false;
      }
      return previous.equals(note.previous);

    }

    @Override
    public int hashCode() {
      int result = id;
      result = 31 * result + line.hashCode();
      result = 31 * result + referenceLine.hashCode();
      result = 31 * result + original.hashCode();
      result = 31 * result + type.hashCode();
      result = 31 * result + alternatives.hashCode();
      result = 31 * result + previous.hashCode();
      return result;
    }

    @Override
    public String toString() {
      final StringBuilder sb = new StringBuilder("Note { ");
      sb.append("id=").append(id);
      sb.append(", line='").append(line).append('\'');
      sb.append(", referenceLine='").append(referenceLine).append('\'');
      sb.append(", original='").append(original).append('\'');
      sb.append(", type='").append(type).append('\'');
      sb.append(", previous='").append(previous).append('\'');
      if (alternatives != null)
      for (Alternative alternative: alternatives) {
        sb.append("\n\t").append(alternative);
      }
      sb.append('}');
      return sb.toString();
    }

    public String getVRI() {
      for (Alternative alternative: alternatives) {
        if (Version.VIPASSANA_RESEARCH_INSTITUT == Version.toVersion(alternative.sourceAbbreviation)) {
          return alternative.text;
        }
      }
      return null;
    }
  }

  public static class Alternative
  {
    @JacksonXmlProperty(isAttribute = true, localName = "source-abbr")
    public String sourceAbbreviation;

    @JacksonXmlProperty(isAttribute = true)
    public String source;

    @JacksonXmlText
    public String text;

    @Override
    public boolean equals(final Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof Alternative)) {
        return false;
      }

      Alternative that = (Alternative) o;

      if (!sourceAbbreviation.equals(that.sourceAbbreviation)) {
        return false;
      }
      return text.equals(that.text);

    }

    @Override
    public int hashCode() {
      int result = sourceAbbreviation.hashCode();
      result = 31 * result + text.hashCode();
      return result;
    }

    @Override
    public String toString() {
      final StringBuilder sb = new StringBuilder("Alternative{");
      sb.append("lang='").append(sourceAbbreviation).append('\'');
      sb.append(", text='").append(text).append('\'');
      sb.append('}');
      return sb.toString();
    }
  }


  @JacksonXmlProperty(isAttribute = true, localName = "archive-path")
  public String archivePath;

  Map<String, List<Note>> perLine = new LinkedHashMap<>();
  List<Note> notes = new LinkedList<>();

  @JacksonXmlProperty(localName = "note")
  public void addNote(Note note) {
    if (note.id != this.notes.size()) {
      throw new IllegalArgumentException("mismatched id: " + note + " expected " + notes.size());
    }
    this.notes.add(note);

    List<Note> notes = perLine.get(note.referenceLine);
    if (notes == null) {
      notes = new LinkedList<>();
      perLine.put(note.referenceLine, notes);
    }
    notes.add(note);
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
