package org.tipitaka.archive.notes;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import org.tipitaka.archive.Fuzzy;
import org.tipitaka.archive.legacy.NoopBuilder;
import org.tipitaka.archive.model.Document;

public class NotesBuilder extends NoopBuilder
{

  public static final String NOTE = "__NOTE__";

  private final Notes notes;

  public NotesBuilder(Writer writer) {
    super(writer);
    this.notes = new Notes();
  }

  @Override
  public void documentStart(final Document document) throws IOException {
    state.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
    state.append("<notes archive-path=\"").append(document.getPath()).append("\">\n");

    state.nextNumber();
    state.nextNumber();
  }

  @Override
  public void documentEnd() throws IOException {
    state.append("</notes>\n");
  }

  @Override
  public void paraStart(String name, String number) throws IOException {
    state.nextLineNumber();
  }

  @Override
  public void noteStart() throws IOException {
    state.push(NOTE);
  }

  @Override
  public void text(String text) throws IOException {
    if (NOTE.equals(state.peek())) {
      String previous = state.getPreviousText();
      int id = state.getId();
      state.append("  <note line=\"").append(state.nextNumber())
          .append("\" original=\"").append(text)
          .append("\" reference-line=\"").append(state.getLineNumber())
          .append("\" id=\"").append(state.nextId()).append("\">\n");
      if (text.contains(")") && !text.startsWith("(")) {
        Note note = notes.get(id);
        boolean isManual = note != null && Type.manual == note.type;

        LinkedList<String> sections = new LinkedList<>();
        String extraNote = fillSections(text, sections);
        state.nextNumber();
        state.append("    <extra>").append(extraNote).append("</extra>\n");
        state.nextNumber();
        state.append("    <alternatives>\n");
        Type type = appendAlternatives(sections, previous, isManual ? note.getVRI() : null);
        state.nextNumber();
        state.append("    </alternatives>\n");
        state.nextNumber();
        state.append("    <type>").append(type.name()).append("</type>\n");
        if (!previous.trim().isEmpty()) {
          state.nextNumber();
          state.append("    <snippet>").append(previous.replace("‘‘", "")).append("</snippet>\n");
        }
      }
      else {
        state.nextNumber();
        state.append("    <type>raw</type>\n");
      }
      state.nextNumber();
      state.append("  </note>\n");
    }
    else {
      state.setPreviousText(text);
    }
  }

  private final static Pattern ALT_TEXT = Pattern.compile("^([^()]*)\\((([^()]+[.]?[^()]*)|\\?)\\)$");

  private String fillSections(String current, List<String> sections) {
    String last = current;
    for (int i = current.indexOf(')'); i > -1; i = current.indexOf(')')) {
      sections.add(current.substring(0, i + 1));
      last = current.substring(i + 1);
      current = last.replaceFirst("^, ?", "");
    }
    if (last.length() > 0) {
      return last;
    }
    return "";
  }

  private Type appendAlternatives(final List<String> sections, final String previous, final String manual) throws IOException {
    int count = 0;
    if (manual != null) {
      count ++;
      state.nextNumber();
      state.append("      <alternative source-abbr=\"").append(Version.VIPASSANA_RESEARCH_INSTITUT.getAbbrevation())
          .append("\" source=\"").append(Version.VIPASSANA_RESEARCH_INSTITUT.getName()).append("\">")
          .append(manual).append("</alternative>\n");
    }

    Type type = manual == null ? Type.no_match : Type.manual;
    String found = null;
    for (String section : sections) {
      Matcher matcher = ALT_TEXT.matcher(section);
      if (matcher.matches() && !matcher.group(1).trim().isEmpty()) {
        if (found == null) {
          final String altText = matcher.group(1).trim();
          found = Fuzzy.findMatchingText(altText,
              previous.trim().replaceAll("–|’’$", "").replaceFirst("^.*[;]", "").replaceAll("[,\\.‘]", "")
                  .replaceAll("  ", " ").trim());
          if (found != null && manual == null) {
            if (previous.endsWith("’’ti ") && found.endsWith("ti")) {
              found = found.substring(0, found.length() - 2) + "’’ti";
            }
            int index = found.indexOf(" ");
            if (index > -1) {
              found = previous.substring(previous.indexOf(found.substring(0, index))).trim();
            }
            type = Type.auto;
            count ++;
            state.nextNumber();
            state.append("      <alternative source-abbr=\"").append(Version.VIPASSANA_RESEARCH_INSTITUT.getAbbrevation())
                .append("\" source=\"").append(Version.VIPASSANA_RESEARCH_INSTITUT.getName()).append("\">")
                .append(found).append("</alternative>\n");
          }
        }

        String altText = matcher.group(1).trim();
        count ++;
        if (previous.endsWith("’’ti ") && altText.contains("ti") && found != null && found.endsWith("’’ti")) {
          int index = altText.lastIndexOf("ti");
          altText = altText.substring(0, index) + "’’" + altText.substring(index);
        }
        String name = matcher.group(2);

        boolean first = true;
        for(String part: name.split(" ")) {
          Version version = Version.toVersion(part);
          if (version == null) {
            // break the loop after using the name as source
            System.err.println("notes source: " + name + " " + type + " " + sections);
            if (first) part = name;
          }
          state.nextNumber();
          state.append("      <alternative source-abbr=\"").append(version == null ? part: version.getAbbrevation())
              .append("\" source=\"").append(version == null ? part : version.getName())
              .append("\">").append(altText)
              .append("</alternative>\n");
          if (first && name == part) {
            break;
          }
          first = false;
        }
      }
    }
    if (count == 0) {
      state.nextNumber();
      state.append("      <alternative source-abbr=\"dummy\" source=\"dummy\"></alternative>\n");
    }
    return type;
  }

  @Override
  public void noteEnd() throws IOException {
    state.pop();
  }

}
