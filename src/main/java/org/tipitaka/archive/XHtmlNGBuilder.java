package org.tipitaka.archive;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by cmeier on 4/1/16.
 */
public class XHtmlNGBuilder
    extends AbstractNGBuilder
{

  public static final String VRI = "vri";

  private String script;

  private String extension;

  private boolean isSubdir;

  private String name;

  private String dir;

  private Map<String, String> breadCrumbs;

  private String file;

  private Map<String, String> versions;

  private String current;

  private boolean omit = true;

  private String source;

  private String extra;

  private boolean first;

  static public class BuilderFactory
      extends NGBuilderFactory {

    public BuilderFactory() throws IOException {
      this(new Layout());
    }

    public BuilderFactory(Layout layout) throws IOException {
      super(layout);
    }

    @Override
    public XHtmlNGBuilder create(final Writer writer) {
      return new XHtmlNGBuilder(writer, this);
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
    visitor.accept(new OutputStreamWriter(System.out), "roman" + file + ".html", null);
  }

  public XHtmlNGBuilder(Writer writer, NGBuilderFactory factory) {
    super(writer, factory);
  }

  protected void pStart(String clazz, String number) throws IOException {
    state.appendIndent("<div class=\"").append(clazz).append("\">");
    String lineNumber = state.nextNumber();
    state.append("<a class=\"line-number\" name=\"line").append(lineNumber).append("\" href=\"")
  //      .append("/").append(state.builder.script.name)
    //    .append(state.builder.path)
      .append("#line").append(lineNumber).append("\">")
        .append(lineNumber).append("</a>");
    if (number != null) {
      //isAnchor = true;
      state.append("<a class=\"bold\" name=\"para").append(number).append("\" href=\"")
        //  .append("/").append(state.builder.script.name)
          //.append(state.builder.path)
          .append("#para").append(number).append("\">").append(number).append(".").append("</a>");
    }
    omit = false;
  }

  protected void pEnd(final String clazz) throws IOException {
    state.appendEnd("</div>");
    omit = true;
  }

  @Override
  public void init(final String extension, final String version) throws IOException {
    this.extension = extension;
    this.current = version == null ? VRI : version;
  }

  @Override
  public void startDocument() throws IOException {
    state.appendLine("<!DOCTYPE HTML>")
        .appendLine("<html>").indent();
  }

  @Override
  public void endDocument() throws IOException {
    state.outdent().appendLine("</html>");
  }

  @Override
  public void startMetadata() throws IOException {
    state.appendLine("<head>").indent()
        .appendLine("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
  }

  @Override
  public void endMetadata() throws IOException {
    state.outdent().appendLine("</head>");
  }

  @Override
  public void normativeSource(final String url) throws IOException {
    state.appendIndent("<meta name=\"normative-source\" content=\"").append(url).appendEnd("\" />");
  }

  @Override
  public void source(final String source) throws IOException {
    state.appendIndent("<meta name=\"source\" content=\"/").append(source).appendEnd("\" />");
  }

  @Override
  public void script(final String script) throws IOException {
    this.script = script;

    state.appendIndent("<meta name=\"script\" content=\"").append(script).appendEnd("\" />");
  }

  @Override
  public void directory(final String dir) throws IOException {
    this.dir = dir;

    state.appendIndent("<meta name=\"directory\" content=\"").append(dir).appendEnd("\" />");
  }

  @Override
  public void basename(final String basename) throws IOException {
    this.name = basename;
    this.file = this.dir + "/" + this.name;
    this.isSubdir = factory.getDirectory().fileOf(file) == null;
    this.breadCrumbs = factory.getDirectory().breadCrumbs(factory.script(script), file);

    state.appendIndent("<meta name=\"basename\" content=\"").append(basename).appendEnd("\" />");
    state.appendIndent("<meta name=\"extension\" content=\"").append(extension.substring(1)).appendEnd("\" />");
    state.appendIndent("<meta name=\"fullpath\" content=\"/").append(script)
        .append(dir).append("/").append(basename).append(extension).appendEnd("\" />");
  }

  @Override
  public void title(final String title) throws IOException {
    state.appendIndent("<link rel=\"stylesheet\" href=\"/").append(script).appendEnd("/style.css\" />")
      .appendIndent("<title>").append(title).appendEnd("</title>");
  }

  @Override
  public void startContent() throws IOException {
    state.appendLine("<body>").indent()
        .appendLine("<div class=\"navigation-container\">").indent();

    appendVersions();
    appendMainMenu();

    state.appendLine("<div class=\"navigation\">").indent();

    state.appendLine("<div class=\"menu\">").indent();
    state.appendIndent("<div class=\"current\">")
        .append("<a href=\"").append("/index.html\">ROOT</a>")
        .appendEnd("</div>");
    state.outdent().appendLine("</div>");

    appendSeparator();

    state.appendLine("<div class=\"menu\">").indent();
    state.appendIndent("<div class=\"current\">");
    if ("/".equals(dir)) {
      state.append(script);
    }
    else {
      state.append("<a href=\"").append("/").append(script).append("/index.html\">").append(script)
          .append("</a>");
    }
    state.appendEnd("</div>");
    state.outdent().appendLine("</div>");

    List<Entry<String, String>> crumbs = new LinkedList<>(breadCrumbs.entrySet());
    for (Map.Entry<String, String> part : crumbs) {
      if (part.getValue() != null) {
        appendMenu(part.getKey());
      }
    }

    state.outdent().appendLine("</div>")
        .outdent().appendLine("</div>")
        .appendLine("<div class=\"document-container\">").indent()
        .appendLine("<div class=\"document\">").indent();
  }

  private void appendVersions() throws IOException {
    state.appendLine("<div class=\"versions\">").indent()
        .appendLine("<span>version:</span>").appendLine("<span class=\"menu\">").indent();

    for (Entry<String, String> entry: versions.entrySet()) {
      String key = RomanScriptHelper.removeDiacritcals(entry.getValue());
      boolean isCurrent = key.equals(current) || entry.getKey().equals(current);
      if (isCurrent) {
        state.appendIndent("<div class=\"current\">");
      }
      else {
        state.appendIndent("<div>");
      }
      if (isCurrent && VRI.equals(current)) {
        state.append("<a href=\"\">");
      }
      else {
        state.append("<a href=\"?version=").append(key).append("\">");
      }
      state.append(entry.getValue()).appendEnd("</a></div>");
    }
    state.outdent().appendLine("</span>")
        .outdent().appendLine("</div>");
  }

  private void appendMainMenu() throws IOException {
    state.appendLine("<div class=\"main-menu\">").indent()
        .appendLine("<a target=\"_blank\" href=\"/impressum.html\">impressum</a>")
        .appendLine("<a target=\"_blank\" href=\"/help.html\">help</a>")
        .appendLine("<a target=\"_blank\" href=\"http://blog.tipitaka.de\">blog</a>");
    if (name.length() > 0 && !isSubdir) {
      // TODO do not follow
      state.appendIndent("<a target=\"_blank\" href=\"")
          .append(name).append(".xml").appendEnd("\">XML format (alpha)</a>");
      state.appendIndent("<a target=\"_blank\" href=\"").append(name)
          .appendEnd(".tei\">TEI format from tipitaka.org (beta)</a>");
    }
    state.outdent().appendLine("</div>");
  }

  private void appendMenu(final String current) throws IOException {
    appendSeparator();
    String name = current.replaceFirst("[^/]+$","");
    state.appendLine("<div class=\"menu\">").indent();
    for (Entry<String, String> entry : factory.getDirectory().list(factory.script(script), name).entrySet()) {

      state.appendIndent("<div");
      state.append(" class=\"");
      if (current.equals(entry.getKey())) {
        state.append("current");
      }
      else {
        state.append("other");
      }
      state.append("\">");
      boolean isDir = dir.startsWith(entry.getKey());
      if (!isDir && file.equals(entry.getKey())) {
            state.append(entry.getValue());
      }
      else {
        state.append("<a href=\"").append("/").append(script).append(entry.getKey());
        if (isDir || !entry.getKey().startsWith(dir)) {
          //!dir.startsWith(entry.getKey())) {
          state.append("/index");
        }
        state.append(extension).append("\">").append(entry.getValue()).append("</a>");
      }
      state.appendEnd("</div>");
    }
    state.outdent().appendLine("</div>");
  }

  private void appendSeparator() throws IOException {state.appendLine("<span> - </span>");}

  @Override
  public void endContent() throws IOException {
    state.outdent().appendLine("</div>")
        .outdent().appendLine("</div>")
        .outdent().appendLine("</body>");
  }

  @Override
  public void startNikaya() throws IOException {
    pStart("nikaya", null);

  }

  @Override
  public void endNikaya() throws IOException {
    pEnd("nikaya");
  }

  @Override
  public void startBook() throws IOException {
    pStart("book", null);
  }

  @Override
  public void endBook() throws IOException {
    pEnd("book");
  }

  @Override
  public void startChapter() throws IOException {
    pStart("chapter", null);
  }

  @Override
  public void endChapter() throws IOException {
    pEnd("chapter");
  }

  @Override
  public void startCentered() throws IOException {
    pStart("centered", null);
  }

  @Override
  public void endCentered() throws IOException {
    pEnd("centered");
  }

  @Override
  public void startSubhead() throws IOException {
    pStart("subhead", null);
  }

  @Override
  public void endSubhead() throws IOException {
    pEnd("subhead");
  }

  @Override
  public void startIndent() throws IOException {
    pStart("indent", null);
  }

  @Override
  public void endIndent() throws IOException {
    pEnd("indent");
  }

  @Override
  public void startBold() throws IOException {
    state.append("<span class=\"bold\">");
  }

  @Override
  public void endBold() throws IOException {
    state.append("</span>");
  }

  @Override
  public void startParagraph(final String number) throws IOException {
    pStart("paragraph", number);
  }

  @Override
  public void endParagraph() throws IOException {
    pEnd("paragraph");
  }

  @Override
  public void startHangnum(final String number) throws IOException {
    state.append("<span class=\"hangnum\">");
  }

  @Override
  public void endHangnum() throws IOException {
    state.append("</span>");
  }

  @Override
  public void startGatha(final String number) throws IOException {
    pStart("gatha" + number, null);
  }

  @Override
  public void endGatha(final String number) throws IOException {
    pEnd("gatha" + number);
  }

  @Override
  public void pageBreak(final Edition edition, final String number) throws IOException {

  }

  @Override
  public void startNote() throws IOException {
    state.append("<span class=\"note\">[");
  }

  @Override
  public void endNote() throws IOException {
    state.append("]</span>");
  }

  @Override
  public void startAlternatives(final String extra, final boolean hasSeparator) throws IOException {
    state.append("<div class=\"tooltip\">");
    this.extra = extra;
    this.first = true;
  }

  @Override
  public void endAlternatives() throws IOException {
    state.outdent().appendLine("</div>").outdent().appendIndent("</div>");
    if (extra != null && extra.length() > 0) {
      state.append("<span class=\"note\">").append(extra).append("</span>");
      this.extra = null;
    }
  }

  @Override
  public void beginAlternative(final String abbr, final String source) throws IOException {
    this.source = source;
    if (current.equals(abbr) || current.equals(source)) {
      first = true;
    }
    else {
      if (first) state.append("\n").indent().appendLine("<div class=\"tooltiptext\">").indent();
      first = false;
    }
  }

  @Override
  public void finalizeAlternative(final String text) throws IOException {
    if (first) {
      state.append(text);
    }
    else {
      state.appendIndent("<div>").append(text).append(" [").append(this.source).append("]").appendEnd("</div>");
    }
  }

  @Override
  public void startTitle() throws IOException {
    pStart("title", null);
  }

  @Override
  public void endTitle() throws IOException {
    pEnd("title");
  }

  @Override
  public void startVersions() {
    this.versions = new LinkedHashMap<>();
    this.versions.put("all", "all versions annotated");
  }

  @Override
  public void addVersion(final String abbr, final String source) {
    this.versions.put(abbr, source);
  }

  @Override
  public void endVersions() {
  }

  @Override
  public void text(final String text) throws IOException {
    if (!this.omit) {
      state.append(text);
    }
  }

}
