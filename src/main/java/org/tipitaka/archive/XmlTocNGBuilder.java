package org.tipitaka.archive;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.tipitaka.archive.Notes.Version;

/**
 * Created by cmeier on 4/1/16.
 */
public class XmlTocNGBuilder
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
    public XmlTocNGBuilder create(final Writer writer) {
      return new XmlTocNGBuilder(writer, this);
    }
  }
  static public void main(String... args) throws Exception {
    BuilderFactory factory = new BuilderFactory();
    NGVisitor visitor = new DirectoryNGVisitor(factory);
    //visitor.accept(new OutputStreamWriter(System.out), "/index.xml", null);
    //visitor.accept(new OutputStreamWriter(System.out), "/roman/index.xml", null);
    visitor.accept(new OutputStreamWriter(System.out), "/roman/tipitaka (mula)/vinayapitaka/parajikapali/index.xml", null);
    //visitor.accept(new OutputStreamWriter(System.out), "/roman/tipitaka (mula)/vinayapitaka/index.xml", null);
    String file =
        //"/tipitaka (mula)/vinayapitaka/parajikapali/veranjakandam";
        "/tipitaka (mula)/vinayapitaka/pacittiyapali/5. pacittiyakandam";
    //"/tipitaka (mula)/vinayapitaka/parajikapali/1. parajikakandam";
    // "/tipitaka (mula)/vinayapitaka/parajikapali/2. sanghadisesakandam";
   // visitor.accept(new OutputStreamWriter(System.out), "roman" + file + ".html", null);
  }

  public XmlTocNGBuilder(Writer writer, NGBuilderFactory factory) {
    super(writer, factory);
  }

  @Override
  public void init(final String extension, final String version) throws IOException {
    this.extension = extension;
    this.current = version;
  }

  @Override
  public void startDocument() throws IOException {
    state.appendLine("<?xml version='1.0' encoding='utf-8' ?>")
        .appendLine("<toc>").indent();
  }

  @Override
  public void endDocument() throws IOException {
    state.outdent().appendLine("</toc>");
  }

  @Override
  public void startMetadata() throws IOException {
    state.appendLine("<head>").indent();
  }

  @Override
  public void endMetadata() throws IOException {
    state.outdent().appendLine("</head>");
  }

  @Override
  public void normativeSource(final String url) throws IOException {
  }

  @Override
  public void source(final String source) throws IOException {
  }

  @Override
  public void script(final String script) throws IOException {
    this.script = script;

    state.appendIndent("<script>").append(script).appendEnd("</script>");
  }

  @Override
  public void directory(final String dir) throws IOException {
    this.dir = dir;

    state.appendIndent("<directory>").append(dir).appendEnd("</directory>");
  }

  @Override
  public void basename(final String basename) throws IOException {
    this.name = basename;
    this.file = (this.dir  == null ? "" : this.dir) + "/" + this.name;
    this.isSubdir = factory.getDirectory().fileOf(file) == null;
    this.breadCrumbs = factory.getDirectory().breadCrumbs(factory.script(script), file);

    state.appendIndent("<basename>").append(basename).appendEnd("</basename>");
    state.appendIndent("<extension>").append(extension.substring(1)).appendEnd("</extension>");
    state.appendIndent("<version>")
        .append(current == null ? Version.VIPASSANA_RESEARCH_INSTITUT.getName() : current).appendEnd("</version>");
    state.appendIndent("<fullpath>");
    if (dir != null) {
      state.append("/").append(script).append(dir);
    }
    state.append("/").append(basename).append(extension);
    if (current != null) {
      state.append("?version=").append(current);
    }
    state.appendEnd("</fullpath>");
  }

  @Override
  public void title(final String title) throws IOException {
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

    List<Entry<String, String>> crumbs = new LinkedList<>(breadCrumbs.entrySet());
    state.appendLine("<menu>").indent();
    state.appendLine("<entry>").indent();
    state.appendIndent("<ref>").append("/index").append(extension).append(postfix).appendEnd("</ref>");

    state.appendIndent("<name>").append("Home").appendEnd("</name>");
    //state.outdent().appendLine("</menu>");

    if (dir != null) {
      state.appendLine("<menu>").indent();
      state.appendLine("<entry>").indent();
      state.appendIndent("<ref>").append("/").append(script).append("/index").append(extension).append(postfix)
            .appendEnd("</ref>");
      state.appendIndent("<name>").append(script).appendEnd("</name>");

      Iterator<Entry<String, String>> iterator = crumbs.iterator();
      appendMenu(iterator, postfix);

      //for (Entry<String, String> part : crumbs) {
      //  if (part.getValue() != null) {
      //    appendMenu(part.getKey(), postfix, --pos);
      //  }
      //}

      //if (name.equals("index") && dir != null) {
      //  appendMenu(file.substring(0, file.lastIndexOf('/') + 1), postfix, 0);
      //}

      state.outdent().appendLine("</entry>");
      state.outdent().appendLine("</menu>");
    }
    state.outdent().appendLine("</entry>");
    state.outdent().appendLine("</menu>");
  }



  private void appendMenu(final Iterator<Entry<String, String>> iterator, final String postfix) throws IOException {
    if (iterator.hasNext()) {
      Entry<String, String> part = iterator.next();
      if (part.getValue() != null) {
        appendMenu(iterator, part.getKey(), postfix);
      }
      //appendMenu(iterator, postfix, pos - 1);
    }
    else {
      if (name.equals("index") && dir != null) {
        state.append("TODO");
        appendMenu(iterator, file.substring(0, file.lastIndexOf('/') + 1), postfix);
      }
    }
  }
  private void appendMenu(final Iterator<Entry<String, String>> iterator, final String current, final String postfix) throws IOException {
    String name = current.replaceFirst("[^/]+$", "");
    state.appendLine("<menu>").indent();
    for (Entry<String, String> entry : factory.getDirectory().list(factory.script(script), name).entrySet()) {

      state.appendLine("<entry>").indent();
      //final String tag;
      //if (current.equals(entry.getKey())) {
      //  tag = "current";
      //}
      //else {
      //  tag = "entry";
      //}
      //state.append(tag).appendEnd(">").indent();
      boolean isDir = dir.startsWith(entry.getKey());
      //System.err.println(dir + " " + isDir + " " + entry.getKey() + " " + factory.getDirectory().fileOf(entry.getKey()));
      if (!isDir && file.equals(entry.getKey()) || (isDir && file.replace("/index", "").equals(entry.getKey()))) {
        state.appendIndent("<ref>");

        if (dir != null) {
          state.append("/").append(script).append(dir);
        }
        state.append("/").append(this.name).append(extension);
        if (this.current != null) {
          state.append("?version=").append(this.current);
        }
        state.appendEnd("</ref>");
        state.appendIndent("<name>").append(entry.getValue()).appendEnd("</name>");
        if (this.name.equals("index") && dir != null) {
          appendMenu(iterator, file.substring(0, file.lastIndexOf('/') + 1), postfix);
        }
      }
      else {
        state.appendIndent("<ref>").append("/").append(script).append(entry.getKey());
        if (isDir || !entry.getKey().startsWith(dir) || factory.getDirectory().fileOf(entry.getKey()) == null) {
          state.append("/index");
        }
        state.append(extension).append(postfix).appendEnd("</ref>")
            .appendIndent("<name>").append(entry.getValue()).appendEnd("</name>");
      }
      if (current.equals(entry.getKey())) {
        appendMenu(iterator, postfix);
      }
      state.outdent().appendLine("</entry>");
      //state.outdent().appendIndent("</").append(tag).appendEnd(">");
    }
    state.outdent().appendLine("</menu>");
  }

  @Override
  public void endContent() throws IOException {
  }

  @Override
  public void startNikaya() throws IOException {
  }

  @Override
  public void endNikaya() throws IOException {
  }

  @Override
  public void startBook() throws IOException {
  }

  @Override
  public void endBook() throws IOException {
  }

  @Override
  public void startChapter() throws IOException {
  }

  @Override
  public void endChapter() throws IOException {
  }

  @Override
  public void startCentered() throws IOException {
  }

  @Override
  public void endCentered() throws IOException {
  }

  @Override
  public void startSubhead() throws IOException {
  }

  @Override
  public void endSubhead() throws IOException {
  }

  @Override
  public void startIndent() throws IOException {
  }

  @Override
  public void endIndent() throws IOException {
  }

  @Override
  public void startBold() throws IOException {
  }

  @Override
  public void endBold() throws IOException {
  }

  @Override
  public void startParagraph(final String number) throws IOException {
  }

  @Override
  public void endParagraph() throws IOException {
  }

  @Override
  public void startHangnum(final String number) throws IOException {
  }

  @Override
  public void endHangnum() throws IOException {
  }

  @Override
  public void startGatha(final String number) throws IOException {
  }

  @Override
  public void endGatha(final String number) throws IOException {
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
  public void startAlternatives(final String extra, final boolean hasSeparator, final String line) throws IOException {
    state.append("<div class=\"tooltip\">");
    // always use separator for consistent look and feel
    this.alternative = new Alternative(extra, true, line);
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
  }

  @Override
  public void endTitle() throws IOException {
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
  }

}
