package org.tipitaka.archive;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.tipitaka.archive.Notes.Alternative;
import org.tipitaka.archive.Notes.Note;
import org.tipitaka.archive.Notes.Type;
import org.tipitaka.search.Script;
import org.tipitaka.search.ScriptFactory;

public class XmlBuilder
     extends NoopBuilder
{

  public static final String NOTE = "__NOTE__";

  public static final String OMIT = "__OMIT__";

  private final Notes notes;

  static public class XmlBuilderFactory extends BuilderFactory<XmlBuilder> {

    private final File notesBasedir;

    public XmlBuilderFactory() throws IOException {
      this(new Layout());
    }

    private XmlBuilderFactory(Layout layout) throws IOException {
      super(layout);
      this.notesBasedir = layout.notesArchive();
    }

    @Override
    public XmlBuilder create(final Writer writer) {
      return new XmlBuilder(writer, this);
    }

    @Override
    public XmlBuilder create(File file, final Script script, final String path) throws IOException {
      ObjectMapper xmlMapper = new XmlMapper();
      File notesFile = Paths.get(notesBasedir.getPath(), script.name, path + "-notes.xml").toFile();
      Notes notes = new Notes();
      if (notesFile != null && notesFile.exists()) {
        try {
          notes = xmlMapper.readValue(new FileReader(notesFile), Notes.class);
        }
        catch (IOException e) {
          // ignore and create a new one as notes list can be empty
        }
      }
      return new XmlBuilder(notes, file, this);
    }

    @Override
    public File archivePath(final Script script, final String path) {
      return new File(script.name, path + ".xml");
    }
  }

  static public void main(String... args) throws Exception {

    TipitakaVisitor visitor = new TipitakaVisitor(new XmlBuilderFactory());

    String file =
        "/tipitaka (mula)/vinayapitaka/parajikapali/veranjakandam";
        //"/tipitaka (mula)/vinayapitaka/parajikapali/1. parajikakandam";
   // "/tipitaka (mula)/vinayapitaka/parajikapali/2. sanghadisesakandam";
    visitor.accept(new OutputStreamWriter(System.out), new ScriptFactory().script("romn"), file);
    //File datafile = new File("../tipitaka-archive/target/data.xml");
    //visitor.accept(datafile, new ScriptFactory().script("romn"), file);

    visitor.accept(new Layout().dataArchive(), new ScriptFactory().script("romn"));

  }

  public XmlBuilder(Notes notes, File file, BuilderFactory factory) throws IOException {
    super(new FileWriter(file), factory);
    this.notes = notes;
  }

  public XmlBuilder(Writer writer, BuilderFactory factory) {
    super(writer, factory);
    ObjectMapper xmlMapper = new XmlMapper();
    //File file = new File("../tipitaka-archive/archive/notes/roman/tipitaka (mula)/vinayapitaka/parajikapali/1. parajikakandam-notes.xml");
    File file = new File("../tipitaka-archive/archive/notes/roman/tipitaka (mula)/vinayapitaka/parajikapali/veranjakandam-notes.xml");
    Notes notes = new Notes();
    if (file != null && file.exists()) {
      try {
        notes = xmlMapper.readValue(new FileReader(file), Notes.class);
      }
      catch (IOException e) {
        // ignore and create a new one, could be empty notes
      }
    }
    this.notes = notes;
  }

  public Builder appendTitle(Map<String, String> breadCrumbs) throws IOException {
    state.append("<title>");
    boolean first = true;
    List<String> parts = new LinkedList<>(breadCrumbs.values());
    Collections.reverse(parts);
    for (String part : parts) {
      if (part != null) {
        if (first) {
          first = false;
        }
        else {
          state.append(" - ");
        }
        state.append(part);
      }
    }
    state.append("</title>");
    return this;
  }

  public void documentStart(Script script, String path) throws IOException {
    String legacy = this.factory.getDirectory().fileOf(path);
    URL url = this.factory.getUrlFactory().normativeURL(script, legacy);
    state.append("<?xml version=\"1.0\"?>\n").append("<document>\n<metadata>\n");

    state.append("<normativeSource>").append(url.toString()).append("</normativeSource>\n");
    state.append("<archivePath>").append(factory.archivePath(script, path).getPath()).append("</archivePath>\n");

    appendTitle(factory.getDirectory().breadCrumbs(script, path));

    state.append("\n</metadata>\n");
    state.append("<body>\n");
  }

  public void documentEnd() throws IOException {
    state.append("</body>\n</document>");
  }

  public  void highlightStart(String name) throws IOException {
    if ("paranum".equals(name) || "dot".equals(name)) {
      state.push(OMIT);
    }
    else {
      state.append("<").append(name).append(">");
      state.push(name);
    }
  }

  public void highlightEnd() throws IOException {
    String name = state.pop();
    if (!OMIT.equals(name)) {
      state.append("</").append(name).append(">");
    }
  }

  public void paraStart(String name, String number) throws IOException {
    String lineNumber = state.nextLineNumber();
    state.notes = new LinkedList<>(notes.findNotes(lineNumber));
    if ("bodytext".equals(name)) {
      name = "paragraph";
    }
    else if ("centre".equals(name)) {
      name = "centered";
    }
    state.append("<").append(name).append(" line=\"").append(lineNumber).append("\"");
    if (number != null) {
      state.append(" number=\"").append(number).append("\"");
    }
    state.append(">");
    state.push(name);
  }

  public void paraEnd() throws IOException {
    if (state.notes.size() > 0) {
      System.err.println("\nmissed " + state.notes.size() + " notes in line " + state.getLineNumber());
    }
    String name = state.pop();
    if ("bodytext".equals(name)) {
      name = "paragraph";
    }
    state.append("</").append(name).append(">\n");
  }

  public void noteStart() throws IOException {
    state.push(NOTE);
  }

  public void text(String text) throws IOException {
    if (OMIT.equals(state.peek())) {
      return;
    }
    if (text.trim().length() == 0) {
      return;
    }
    if (NOTE.equals(state.peek())) {
      Note note = notes.get(state.getId());
      if (note == null) {
        System.err.println("\n\nno note with id: " + state.getId());
      }
      if (!note.original.equals(text)){
        System.err.println("\n\nnote error text: " + text + " <> " + note.original);
      }
      if (!note.referenceLine.equals(state.getLineNumber())) {
        System.err.println("\n\nnote error line: " + state.getLineNumber() + " <> " + note.referenceLine);
      }
      if (Type.raw == note.type || Type.no_match == note.type) {
        state.notes.pop();
        state.append("<note>");
        state.appendText(text);
        state.append("</note>");
      }
      state.nextId();
    }
    else {
      if (!state.notes.isEmpty()) {
        Note note = state.notes.peek();
        if ((note.type == Type.auto || note.type == Type.manual) && note.getVRI() != null) {
          String vri = note.getVRI();
          if (vri.endsWith("ti")) {
            vri = vri.substring(0, vri.length() - 2) + ".?.?ti";
          }
          Pattern alternatives = Pattern.compile("(.*)" + vri.replace(" ", "[;,– ‘]+")
              .replace("]", "[\\]]").replace("[", "[\\[]")
              .replace(")", "[)]").replace("(", "[(]") + "([. ]*)$");
          Matcher matcher = alternatives.matcher(text);
          if (matcher.matches()) {
            state.append(matcher.group(1));
            state.append("<alternatives line=\"").append(note.line).append("\">");
            for (Alternative alternative : note.alternatives) {
              state.append("<alternative source-abbr=\"").append(alternative.sourceAbbreviation)
                  .append("\" source=\"").append(alternative.source).append("\">")
                  .append(alternative.text).append("</alternative>");
            }
            state.append("</alternatives>");
            state.append(matcher.group(2));
            state.notes.pop();
          }
          else {
            state.appendText(text.replaceFirst("^ *", ""));
          }
        }
      }
      else {
        state.appendText(text.replaceFirst("^ *", ""));
      }
    }
  }

  public void noteEnd() throws IOException {
    state.pop();
  }

  public void pageBreak(String edition, String number) throws IOException {
    // TODO make enum for edition
    state.append("<pageBreak edition=\"").append(edition).append("\" number=\"").append(number).append("\" />");
  }
}
