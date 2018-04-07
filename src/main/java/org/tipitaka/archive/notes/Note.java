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
public class Note
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

  public String extra;

  public String snippet;

  public void setExtra(String value) {
    if (value == null || "null".equals(value)) {
      extra = "";
    }
    else {
      extra = value;
    }
  }
  //String getKey() {
  //  return referenceLine + " " + original;
  //}

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
    if (!extra.equals(note.extra)) {
      return false;
    }
    return snippet.equals(note.snippet);

  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + line.hashCode();
    result = 31 * result + referenceLine.hashCode();
    result = 31 * result + original.hashCode();
    result = 31 * result + type.hashCode();
    result = 31 * result + alternatives.hashCode();
    result = 31 * result + extra.hashCode();
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
    sb.append(", extra='").append(extra).append('\'');
    sb.append(", snippet='").append(snippet).append('\'');
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
