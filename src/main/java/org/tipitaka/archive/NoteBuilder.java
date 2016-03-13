package org.tipitaka.archive;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.tipitaka.archive.Notes.Note;
import org.tipitaka.archive.Notes.Type;
import org.tipitaka.archive.Notes.Version;
import org.tipitaka.search.Script;
import org.tipitaka.search.ScriptFactory;


public class NoteBuilder
    extends NoopBuilder
{

  public static final String NOTE = "__NOTE__";


  private final Notes notes;

  static public class BuilderFactory extends org.tipitaka.archive.BuilderFactory<NoteBuilder> {

    public BuilderFactory() throws IOException {}

    @Override
    public NoteBuilder create(final Writer writer) {
      return new NoteBuilder(writer, this);
    }

    @Override
    public NoteBuilder create(final File file, final Script script, final String path) throws IOException {
      ObjectMapper xmlMapper = new XmlMapper();
      Notes notes = new Notes();
      if (file != null && file.exists()) {
        try {
          notes = xmlMapper.readValue(new FileReader(file), Notes.class);
        }
        catch (IOException e) {
          //e.printStackTrace();
          // ignore and create a new one
        }
      }
      return new NoteBuilder(notes, file, this);
    }

    public File archivePath(Script script, String path) {
      return new File(script.name, path + "-notes.xml");
    }
  }

  static public void main(String... args) throws Exception {

    TipitakaVisitor visitor = new TipitakaVisitor(new BuilderFactory());

    String file =
        //"/tipitaka (mula)/vinayapitaka/parajikapali/veranjakandam";
        "/tipitaka (mula)/vinayapitaka/parajikapali/1. parajikakandam";
    //"/tipitaka (mula)/vinayapitaka/parajikapali/2. sanghadisesakandam";
    visitor.accept(new OutputStreamWriter(System.out), new ScriptFactory().script("romn"), file);
        //visitor.generateAll(new File("archive"), new ScriptFactory().script("romn"));
    Fuzzy.similar("yo kho bhikkhave", "āmantesi yo bhikkhave");
    Fuzzy.similar("yo pana bhikkhave", "āmantesi yo bhikkhave");
    Fuzzy.similar("yo kho bhikkhave", "yo, bhikkhave");
    Fuzzy.similar("yo pana bhikkhave", "yo, bhikkhave");
    Fuzzy.similar("itthī – ‘muhuttaṃ", "itthī taṃ passitvā etadavoca muhuttaṃ");
    for(String i : Fuzzy.results) {
      //System.out.println(i);
    }
    //File notesfile = new File("../tipitaka-archive/target/notes.xml");
    //visitor.accept(notesfile, new ScriptFactory().script("romn"), file);


    visitor.accept(new Layout().notesArchive(), new ScriptFactory().script("romn"));
  }

  public NoteBuilder(Notes notes, File file, BuilderFactory factory) throws IOException {
    super(new FileWriter(file), factory);
    this.notes = notes;
  }

  public NoteBuilder(Writer writer, BuilderFactory factory) {
    super(writer, factory);
    this.notes = new Notes();
  }

  @Override
  public void documentStart(final Script script, final String path) throws IOException {
    state.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
    state.append("<notes archive-path=\"").append(factory.archivePath(script, path).getPath()).append("\">\n");

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

        state.nextNumber();
        state.append("    <alternatives>\n");
        Type type = appendAlternatives(text, previous, isManual ? note.getVRI() : null);
        state.nextNumber();
        state.append("    </alternatives>\n");
        state.nextNumber();
        state.append("    <type>").append(type.name()).append("</type>\n");
        if (!previous.trim().isEmpty()) {
          state.nextNumber();
          state.append("    <previous>").append(previous.replace("‘‘", "")).append("</previous>\n");
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

  private Type appendAlternatives(String text, String previous, String manual) throws IOException {
    LinkedList<String> sections = new LinkedList<>();
    String current = text;
    for (int i = current.indexOf(')'); i > -1; i = current.indexOf(')')) {
      sections.add(current.substring(0, i + 1));
      current = current.substring(i + 1).replaceFirst(", ?", "");
    }

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
            type = Type.auto;
            count ++;
            state.nextNumber();
            state.append("      <alternative source-abbr=\"").append(Version.VIPASSANA_RESEARCH_INSTITUT.getAbbrevation())
                .append("\" source=\"").append(Version.VIPASSANA_RESEARCH_INSTITUT.getName()).append("\">")
                .append(found.replaceFirst("^‘‘", "")).append("</alternative>\n");
          }
        }
        final String altText = matcher.group(1).trim();
        count ++;
        state.nextNumber();
        String name = matcher.group(2);
        Version version = Version.toVersion(name);
        state.append("      <alternative source-abbr=\"").append(version == null ? name : version.getAbbrevation())
            .append("\" source=\"").append(version == null? name : version.getName())
            .append("\">").append(altText)
            .append("</alternative>\n");
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
