package org.tipitaka.archive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import org.tipitaka.archive.Notes.Version;

public class TeiNGBuilder
    extends AbstractNGBuilder
    implements NGBuilder
{

  private String source;

  static public class BuilderFactory
      extends org.tipitaka.archive.BuilderFactory<NGBuilder> {
    private final File notesBasedir;

    public BuilderFactory() throws IOException {
      this(new Layout());
    }

    public BuilderFactory(Layout layout) throws IOException {
      super(layout);
      this.notesBasedir = layout.notesArchive();
    }

    @Override
    public TeiNGBuilder create(final Writer writer) {
      return new TeiNGBuilder(writer, this);
    }

    @Override
    public TeiNGBuilder create(File file, final Script script, final String path) throws IOException {
      File notesFile = Paths.get(notesBasedir.getPath(), script.name, path + "-notes.xml").toFile();
      return new TeiNGBuilder(new OutputStreamWriter(new FileOutputStream(notesFile), "utf-8"), this);
    }

    @Override
    public File archivePath(final Script script, final String path) {
      return new File(script.name, path + ".xml");
    }
  }

  static public void main(String... args) throws Exception {

    BuilderFactory factory = new BuilderFactory();
    NGVisitor visitor = new NGVisitor(factory);
    String file =
        //"/tipitaka (mula)/vinayapitaka/parajikapali/veranjakandam";
        "/tipitaka (mula)/vinayapitaka/pacittiyapali/5. pacittiyakandam";
    //"/tipitaka (mula)/vinayapitaka/parajikapali/1. parajikakandam";
   // "/tipitaka (mula)/vinayapitaka/parajikapali/2. sanghadisesakandam";
    visitor.accept(new OutputStreamWriter(System.out), factory.script("romn"), file);
    //File datafile = new File("../tipitaka-archive/target/data.xml");
    //visitor.accept(datafile, new ScriptFactory().script("romn"), file);

    //visitor.accept(new Layout().dataArchive(), factory.script("romn"));

  }

  public TeiNGBuilder(Writer writer, org.tipitaka.archive.BuilderFactory factory) {
    super(writer, factory);
  }

  boolean omit = true;
  boolean needsSpace = false;
  boolean isNested = false;
  boolean trim = false;
  public void text(String text) throws IOException {
    if (!omit) {
      if (needsSpace && !isNested) {
        needsSpace = false;
      }
      if (trim) {
        trim = false;
        if (text.startsWith("  ") || text.startsWith(" .") || text.startsWith(" ,") || text.startsWith(" ;")) {
          text = text.substring(1);
        }
      }

      state.append(text);
    }
  }

  @Override
  public void startDocument() throws IOException {
    state.append("<?xml version=\"1.0\" encoding=\"UTF-16\"?>\n" +
        "<?xml-stylesheet type=\"text/xsl\" href=\"/tipitaka-latn.xsl\"?>\n" +
        "<TEI.2>\n" +
        "<teiHeader></teiHeader>\n" +
        "<text>\n" +
        "<front></front>\n" +
        "<body>\n");
  }

  @Override
  public void endDocument() throws IOException {
    state.append("</body>\n" +
        "<back></back>\n" +
        "</text>\n" +
        "</TEI.2>\n");
  }

  @Override
  public void startMetadata() throws IOException {

  }

  @Override
  public void endMetadata() throws IOException {

  }

  @Override
  public void normativeSource(final String url) {
    System.err.println(url);
  }

  @Override
  public void archivePath(final String path) {

  }

  @Override
  public void title(final String title) {

  }

  @Override
  public void startContent() throws IOException {
  }

  @Override
  public void endContent() throws IOException {

  }

  @Override
  public void startNikaya() throws IOException {
    startP("nikaya");
  }

  private void startP(final String name) throws IOException {
    omit = false;
    state.append("\n<p rend=\"").append(name).append("\">");
  }

  @Override
  public void endNikaya() throws IOException {
    endP();
  }

  private void endP() throws IOException {
    omit = true;
    state.append("</p>\n");
  }

  @Override
  public void startBook() throws IOException {
    startP("book");
  }

  @Override
  public void endBook() throws IOException {
    endP();
  }

  @Override
  public void startChapter() throws IOException {
    startP("chapter");
  }

  @Override
  public void endChapter() throws IOException {
    endP();
  }

  @Override
  public void startCentered() throws IOException {
    startP("centre");
  }

  @Override
  public void endCentered() throws IOException {
    endP();
  }

  @Override
  public void startSubhead() throws IOException {
    startP("subhead");
  }

  @Override
  public void endSubhead() throws IOException {
    endP();
  }

  @Override
  public void startIndent() throws IOException {
    startP("indent");
  }

  @Override
  public void endIndent() throws IOException {
    endP();
  }

  @Override
  public void startBold() throws IOException {
    state.append("<hi rend=\"bold\">");
    isNested = true;
  }

  @Override
  public void endBold() throws IOException {
    state.append("</hi>");
    isNested = false;
    needsSpace = false;
  }

  private void appendP(final String number, final String name) throws IOException {
    omit = false;
    needsSpace = true;
    state.append("\n<p rend=\"" + name + "\" n=\"").append(number).append("\"><hi rend=\"paranum\">").append(number)
        .append("</hi><hi rend=\"dot\">.</hi>");
  }

  @Override
  public void startParagraph(final String number) throws IOException {
    String name = "bodytext";
    if (number != null) {
      appendP(number, name);
    }
    else {
      startP("bodytext");
    }
  }

  @Override
  public void endParagraph() throws IOException {
    endP();
  }

  @Override
  public void startHangnum(final String number) throws IOException {
    String name = "hangnum";
    appendP(number, name);
  }

  @Override
  public void endHangnum() throws IOException {
    endP();
  }

  @Override
  public void startGatha(final String number) throws IOException {
    state.append("\n<p rend=\"gatha").append(number).append("\">");
    omit = false;
  }

  @Override
  public void endGatha(final String substring) throws IOException {
    endP();
  }

  @Override
  public void pageBreak(final Edition edition, final String number) throws IOException {
    state.append("<pb ed=\"").append(edition.name().substring(0, 1)).append("\" n=\"").append(number).append("\" />");
    needsSpace = true;
  }

  @Override
  public void startNote() throws IOException {
    isNested = true;
    state.append("<note>");
  }

  @Override
  public void endNote() throws IOException {
    isNested = false;
    state.append("</note>");
  }

  static class Alternative {

    private final String extra;

    private final String separator;

    private String text;

    private Map<String, String> map = new LinkedHashMap<>();

    public Alternative(final String extra, final boolean separator) {
      this.extra = "null".equals(extra) ? "" : extra;
      this.separator = separator ? ", " : " ";
    }


    public void setText(final String text) {
      this.text = text;
    }

    public String getText() {
      return this.text;
    }

    public void add(final String version, final String text) {
      if (map.containsKey(text)) {
        String versions = map.get(text) + " " + version;
        map.put(text, versions);
      }
      else {
        map.put(text, version);
      }
    }

    public String getNote() {
      StringBuilder result = new StringBuilder();
      boolean first = true;
      for (Map.Entry entry : map.entrySet()) {
        if (first) {
          first = false;
        }
        else {
          result.append(separator);
        }
        result.append((entry.getKey())).append(" (").append(entry.getValue()).append(")");
      }
      if (extra != null) result.append(extra);
      return result.toString();
    }
  }

  private Alternative alternative;
  @Override
  public void startAlternatives(final String extra, final boolean hasSeparator) throws IOException {
    alternative = new Alternative(extra, hasSeparator);
  }

  @Override
  public void endAlternatives() throws IOException {
    state.append(alternative.getText()).append(" <note>").append(alternative.getNote().replace("’’ti", "ti"))
        .append("</note>");
    alternative = null;
    trim = true;
  }

  @Override
  public void beginAlternative(final String abbr, final String source) {
    this.source = abbr;
  }

  @Override
  public void finalizeAlternative(final String text) {
    if (Version.VIPASSANA_RESEARCH_INSTITUT.getAbbrevation().equals(source)) {
      alternative.setText(text);
    }
    else {
      alternative.add(source, text);
    }
  }

  @Override
  public void startTitle() throws IOException {
    startP("title");
  }

  @Override
  public void endTitle() throws IOException {
    endP();
  }

  @Override
  public void startVersions() {
  }

  @Override
  public void addVersion(final String abbr, final String source) {
  }

  @Override
  public void endVersions() {
  }
}
