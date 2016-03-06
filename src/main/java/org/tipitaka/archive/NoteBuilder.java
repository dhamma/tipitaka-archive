package org.tipitaka.archive;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tipitaka.search.ScriptFactory;
import org.tipitaka.search.TipitakaUrlFactory;
import org.xmlpull.v1.XmlPullParserException;

public class NoteBuilder
    extends NoopBuilder
{

  public static final String NOTE = "__NOTE__";

  static public class BuilderFactory extends org.tipitaka.archive.BuilderFactory<NoteBuilder> {

    public BuilderFactory(final TipitakaUrlFactory urlFactory, final File directoryMap)
        throws XmlPullParserException, IOException
    {
      super(urlFactory, directoryMap);
    }

    @Override
    public NoteBuilder create(final Writer writer) {
      return new NoteBuilder(writer, this);
    }
  }

  static public void main(String... args) throws Exception {
    BuilderFactory factory = new BuilderFactory(new TipitakaUrlFactory("file:../tipitaka-search/solr/tipitaka/"),
        new File("../tipitaka-search/solr/tipitaka/directory.map"));

    TipitakaVisitor visitor = new TipitakaVisitor(factory);

    visitor.accept(new OutputStreamWriter(System.out), new ScriptFactory().script("romn"),
        //    "/tipitaka (mula)/vinayapitaka/parajikapali/veranjakandam");
        "/tipitaka (mula)/vinayapitaka/parajikapali/1. parajikakandam");
        //visitor.generateAll(new File("archive"), new ScriptFactory().script("romn"));
    Fuzzy.similar("yo kho bhikkhave", "āmantesi yo bhikkhave");
    Fuzzy.similar("yo pana bhikkhave", "āmantesi yo bhikkhave");
    Fuzzy.similar("yo kho bhikkhave", "yo, bhikkhave");
    Fuzzy.similar("yo pana bhikkhave", "yo, bhikkhave");
    Fuzzy.similar("itthī – ‘muhuttaṃ", "itthī taṃ passitvā etadavoca muhuttaṃ");
    for(String i : Fuzzy.results) {
      System.out.println(i);
    }
  }

  public NoteBuilder(Writer writer, BuilderFactory factory) {
   super(writer, factory);
  }

  public void noteStart() throws IOException {
    state.push(NOTE);
  }

  public void text(String text) throws IOException {
    if (NOTE.equals(state.peek())) {
      //state.append("<text>");
      //state.appendText(text);
      //state.append("</text>");
      String previous = state.getPreviousText();
      if (text.contains(".)")) {// || text.contains("(?)")) {
        state.append("<alternatives>\n");
        appendAlternatives(text, previous);
        if (!previous.trim().isEmpty()) {
          state.append("  <previous>").append(previous.replace("‘‘", "")).append("</previous>\n");
        }
        state.append("</alternatives>\n");
      }
      else {
        state.append("<note>").append(text).append("</note>\n");
      }
    }
    else {
      state.setPreviousText(text);
    }
  }

  private final static Pattern ALT_TEXT = Pattern.compile("^([^()]*)\\((([^()]+[.][^()]*)|\\?)\\)$");

  private void appendAlternatives(String text, String previous) throws IOException {
    LinkedList<String> sections = new LinkedList<>();
    String current = text;
    for (int i = current.indexOf(')'); i > -1; i = current.indexOf(')')) {
      sections.add(current.substring(0, i + 1));
      current = current.substring(i + 1).replaceFirst(", ?", "");
    }

    String found = null;
    for (String section : sections) {
      Matcher matcher = ALT_TEXT.matcher(section);
      if (matcher.matches()) {
        if (found == null) {
          found = findText(matcher, previous);
          if (found != null) {
            state.append("  <text>").append(found.replaceFirst("^‘‘", "")).append("</text>\n");
          }
        }
        final String altText = matcher.group(1).trim();
        state.append("  <alt lang=\"").append(matcher.group(2)).append("\">").append(altText).append("</alt>\n");
      }
    }
  }

  private String findText(final Matcher matcher, final String previous) throws IOException {
    final String altText = matcher.group(1).trim();
    String[] words = previous.split(" ");
    int count = altText.replaceAll("[^ ]*", "").length() + 1;
    if (count == 13) {
      String pre = words[words.length - 1];
      if (Fuzzy.similar(altText, pre)) {
        return pre.replace("‘‘", "");
      }
    }
    String[] parts = altText.split(" ");
    if (count == 32) {
      String pre1 = words[words.length - 1];
      String pre2 = words[words.length - 2];
      if (Fuzzy.similar(parts[0], pre2) && Fuzzy.similar(parts[1], pre1)) {
        return pre2.replace("‘‘", "") + " " + pre1.replace("‘‘", "");
      }
      if (Fuzzy.similar(altText, pre1)) {
        return pre1.replace("‘‘", "");
      }
    }
    Map<Integer, String> results = new HashMap<>();
    for (int j = count; j > 0; j--) {
      boolean match = true;
      StringBuilder found = new StringBuilder();
      for (int i = 0; i < j; i++) {
        int index = words.length - j + i;
        if (index > -1) {
          match = match && (Fuzzy.similar(parts[i], words[index]));
          found.append(words[index]).append(" ");
        }
        if (!match) {
          break;
        }
      }
      if (match) {
        return found.toString().trim();
      }

      found = new StringBuilder();
      for (int i = 0; i < j; i++) {
        int index = words.length - j + i;
        if (index > -1) {
          found.append(words[index]).append(" ");
        }
      }
      String alt = altText;
      for (int k = count; k > 1; k--) {
        System.err.println(found + " ~ " + alt + " " + Fuzzy.similar(alt, found.toString()));
        if (Fuzzy.similar(alt, found.toString().trim())) {
          return found.toString().trim();
        }
        int index = alt.lastIndexOf(' ');
        alt = alt.substring(0, index);
      }

    }
    return null;
  }

  public void noteEnd() throws IOException {
    state.pop();
  }

}
