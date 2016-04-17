package org.tipitaka.archive;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.tipitaka.archive.Notes.Version;

/**
 * Created by cmeier on 4/1/16.
 */
public class XHtmlNGBuilder
    extends AbstractNGBuilder
{

  public static final String VRI = "vri";

  public static final String ALL = "all";

  public static final String ALL_VERSIONS_ANNOTATED = "all versions annotated";

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

  private Alternative alternative;

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
    NGVisitor visitor = new DirectoryNGVisitor(factory);
    visitor.accept(new OutputStreamWriter(System.out), "/roman/tipitaka (mula)/vinayapitaka/parajikapali/index.html", null);
    visitor.accept(new OutputStreamWriter(System.out), "/roman/tipitaka (mula)/vinayapitaka/index.html", null);
    String file =
        //"/tipitaka (mula)/vinayapitaka/parajikapali/veranjakandam";
        "/tipitaka (mula)/vinayapitaka/pacittiyapali/5. pacittiyakandam";
    //"/tipitaka (mula)/vinayapitaka/parajikapali/1. parajikakandam";
    // "/tipitaka (mula)/vinayapitaka/parajikapali/2. sanghadisesakandam";
   // visitor.accept(new OutputStreamWriter(System.out), "roman" + file + ".html", null);
  }

  public XHtmlNGBuilder(Writer writer, NGBuilderFactory factory) {
    super(writer, factory);
  }

  protected void pStart(String clazz, String number) throws IOException {
    state.appendIndent("<div class=\"").append(clazz).append("\">");
    String lineNumber = state.nextNumber();
    state.append("<a class=\"line-number\" name=\"line").append(lineNumber).append("\" href=\"")
      .append("#line").append(lineNumber).append("\">")
        .append(lineNumber).append("</a>");
    if (number != null) {
      state.append("<a class=\"bold\" name=\"para").append(number).append("\" href=\"")
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
    this.current = version;
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
    this.file = (this.dir  == null ? "" : this.dir) + "/" + this.name;
    this.isSubdir = factory.getDirectory().fileOf(file) == null;
    this.breadCrumbs = factory.getDirectory().breadCrumbs(factory.script(script), file);

    state.appendIndent("<meta name=\"basename\" content=\"").append(basename).appendEnd("\" />");
    state.appendIndent("<meta name=\"extension\" content=\"").append(extension.substring(1)).appendEnd("\" />");
    state.appendIndent("<meta name=\"version\" content=\"")
        .append(current == null ? Version.VIPASSANA_RESEARCH_INSTITUT.getName() : current).appendEnd("\" />");
    state.appendIndent("<meta name=\"fullpath\" content=\"");
    if (dir != null) {
      state.append("/").append(script).append(dir);
    }
    state.append("/").append(basename).append(extension);
    if (current != null) {
      state.append("?version=").append(current);
    }
    state.appendEnd("\" />");
  }

  @Override
  public void title(final String title) throws IOException {
    if (current != null) {
      state.appendLine("<meta name=\"robots\" content=\"noindex, nofollow\" />")
          .appendIndent("<link rel=\"canonical\" href=\"http://www.tipitaka.de");
      if (dir != null) {
        state.append("/").append(script);
      }
      state.append(file).append(extension).appendEnd("\" />");
    }
    state.appendIndent("<link rel=\"stylesheet\" href=\"/").append(script).appendEnd("/style.css\" />");
    state.appendIndent("<title>");
    if (title == null) {
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
    }
    else {
        state.append(title);
    }
    state.appendEnd("</title>");
  }

  @Override
  public void startContent() throws IOException {
    final String postfix = current == null ? "" : "?version=" + current;

    state.appendLine("<body>").indent()
        .appendLine("<div class=\"navigation-container\">").indent();

    appendVersions();
    appendMainMenu();

    state.appendLine("<div class=\"navigation\">").indent();

    state.appendLine("<div class=\"menu\">").indent();
    state.appendIndent("<div class=\"current\">");
    if (dir != null) {
      state.append("<a href=\"").append("/index.html").append(postfix).append("\">ROOT</a>");
    }
    else {
      state.append("ROOT");
    }
    state.appendEnd("</div>");
    state.outdent().appendLine("</div>");

    appendSeparator();

    state.appendLine("<div class=\"menu\">").indent();
    state.appendIndent("<div class=\"current\">");
    if ("".equals(dir)) {
      state.append(script);
    }
    else {
      state.append("<a href=\"").append("/").append(script).append("/index.html").append(postfix)
          .append("\">").append(script).append("</a>");
    }
    state.appendEnd("</div>");
    state.outdent().appendLine("</div>");

    List<Entry<String, String>> crumbs = new LinkedList<>(breadCrumbs.entrySet());
    for (Map.Entry<String, String> part : crumbs) {
      if (part.getValue() != null) {
        appendMenu(part.getKey(), postfix);
      }
    }

    if (file.endsWith("index") && dir != null) {
      appendMenu(file.substring(0, file.lastIndexOf('/') + 1), postfix);
    }

    state.outdent().appendLine("</div>")
        .outdent().appendLine("</div>")
        .appendLine("<div class=\"document-container\">").indent()
        .appendLine("<div class=\"document\">").indent();
  }

  private void appendVersions() throws IOException {
    if (versions == null) return;
    state.appendLine("<div class=\"versions\">").indent()
        .appendLine("<span>version:</span>").appendLine("<span class=\"menu\">").indent();

    for (Entry<String, String> entry: versions.entrySet()) {
      String key = RomanScriptHelper.removeDiacritcals(entry.getValue());
      boolean isVri = entry.getKey().equals(Version.VIPASSANA_RESEARCH_INSTITUT.getAbbrevation());
      boolean isCurrent = key.equals(current) || entry.getKey().equals(current) || (current == null && isVri);
      if (isCurrent) {
        state.appendIndent("<div class=\"current\">");
      }
      else {
        state.appendIndent("<div>");
      }
      if (isCurrent && current == null) {
        state.append("<a>");
      }
      else if (isVri) {
        state.append("<a href=\"").append(name).append(extension).append("\">");
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
    state.appendLine("<div class=\"main-menu\">").indent();
    if (name.length() > 0 && !isSubdir) {
      // TODO do not follow
      state.appendIndent("<a target=\"_blank\" href=\"")
          .append(name).append(".xml").appendEnd("\">XML format (alpha)</a>");
      state.appendIndent("<a target=\"_blank\" href=\"").append(name)
          .appendEnd(".tei\">TEI format from tipitaka.org (beta)</a>");
    }
    state.appendLine("<a target=\"_blank\" href=\"/help.html\">help</a>")
        .appendLine("<a target=\"_blank\" href=\"http://blog.tipitaka.de\">blog</a>")
        .appendLine("<a target=\"_blank\" href=\"/impressum.html\">impressum</a>");
    state.outdent().appendLine("</div>");
  }

  private void appendMenu(final String current, final String postfix) throws IOException {
    appendSeparator();
    String name = current.replaceFirst("[^/]+$", "");
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
      //System.err.println(dir + " " + isDir + " " + entry.getKey() + " " + factory.getDirectory().fileOf(entry.getKey()));
      if (!isDir && file.equals(entry.getKey()) || (isDir && file.replace("/index", "").equals(entry.getKey()))) {
        state.append(entry.getValue());
      }
      else {
        state.append("<a href=\"").append("/").append(script).append(entry.getKey());
        if (isDir || !entry.getKey().startsWith(dir) || factory.getDirectory().fileOf(entry.getKey()) == null) {
          state.append("/index");
        }
        state.append(extension).append(postfix).append("\">").append(entry.getValue()).append("</a>");
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
    // always use separator for consistent look and feel
    this.alternative = new Alternative(extra, true);
  }

  @Override
  public void endAlternatives() throws IOException {
    if (ALL_VERSIONS_ANNOTATED.equals(current)) {
     state.append(alternative.getText()).append(" <span class=\"note\">[")
         .append(alternative.getNote()).append("]</span></div>");
    }
    else {
      state.appendEnd(alternative.getText()).indent();
      state.appendIndent("<div class=\"tooltiptext\" style=\"width:")
          .append(Integer.toString((alternative.longest()+7)/2)).appendEnd("em;\">").indent();
      for (Entry<String, String> entry : alternative.entrySet()) {
        state.appendIndent("<div>").append(entry.getValue()).append(" <span>[").append(entry.getKey()).append("]")
            .appendEnd("</span></div>");
      }
      state.outdent().appendLine("</div>").outdent().appendIndent("</div>");
      if (alternative.getExtra() != null && alternative.getExtra().length() > 0) {
        state.append("<span class=\"note\">[").append(alternative.getExtra()).append("]</span>");
      }
    }
    alternative = null;
  }

  @Override
  public void beginAlternative(final String abbr, final String source) throws IOException {
    this.source = source;
    if ((current == null && abbr.equals(Version.VIPASSANA_RESEARCH_INSTITUT.getAbbrevation())) ||
        (current != null && (current.equals(abbr) || current.equals(source)))) {
      alternative.setCurrent(source);
    }
  }

  @Override
  public void finalizeAlternative(final String text) throws IOException {
    if (ALL_VERSIONS_ANNOTATED.equals(current) && Version.VIPASSANA_RESEARCH_INSTITUT.getName().equals(source)) {
      alternative.setText(text);
    }
    else {
      alternative.add(source, text);
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
    this.versions.put(Version.VIPASSANA_RESEARCH_INSTITUT.getAbbrevation(),
        Version.VIPASSANA_RESEARCH_INSTITUT.getName());
    this.versions.put(ALL, ALL_VERSIONS_ANNOTATED);
  }

  @Override
  public void addVersion(final String abbr, final String source) {
    // avoid double entry of all-version-annotated
    if (!this.versions.containsValue(source)) {
      this.versions.put(abbr, source);
    }
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
