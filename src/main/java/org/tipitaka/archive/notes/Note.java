package org.tipitaka.archive.notes;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

/**
 * Created by cmeier on 3/12/16.
 */
public class Note
{

  @JacksonXmlProperty(isAttribute = true)
  public int id;

  @JacksonXmlProperty(isAttribute = true)
  public int line;

  @JacksonXmlProperty(isAttribute = true, localName = "reference-line")
  public int referenceLine;

  public String original;

  public Type type;

  public List<Alternative> alternatives = new LinkedList<>();

  public String snippet;

  public String hint;

  public String match;

  public List<String> references;

  public void addAlternative(Alternative alt) {
    for(Alternative old: alternatives) {
      if (alt.equals(old)) return;
      if (alt.getVersion().equals(old.getVersion())) {
        System.err.println("[WARN] (version.duplication) " + old + "<->" + alt + " on " + this);
      }
    }
    alternatives.add(alt);
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
    if (line != note.line) {
      return false;
    }
    if (referenceLine != note.referenceLine) {
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
    if (!match.equals(note.match)) {
      return false;
    }
    return snippet.equals(note.snippet);

  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + line;
    result = 31 * result + referenceLine;
    result = 31 * result + original.hashCode();
    result = 31 * result + type.hashCode();
    result = 31 * result + alternatives.hashCode();
    result = 31 * result + hint.hashCode();
    result = 31 * result + match.hashCode();
    result = 31 * result + snippet.hashCode();
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
    sb.append(", hint='").append(hint).append('\'');
    sb.append(", match='").append(match).append('\'');
    sb.append(", snippet='").append(snippet).append('\'');
    if (references != null) {
      sb.append(",  references ").append(references);
    }
    if (alternatives != null) {
      for (Alternative alternative: alternatives) {
        sb.append("\n\t").append(alternative);
      }
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

  public Set<Version> getVersions() {
    return alternatives.stream()
      .map(Alternative::getVersion)
      .collect(Collectors.toSet());
  }
}
