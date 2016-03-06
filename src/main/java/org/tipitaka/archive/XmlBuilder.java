package org.tipitaka.archive;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.tipitaka.search.Script;
import org.tipitaka.search.ScriptFactory;
import org.tipitaka.search.TipitakaUrlFactory;
import org.xmlpull.v1.XmlPullParserException;

public class XmlBuilder
    implements Builder
{

  public static final String NOTE = "__NOTE__";

  public static final String OMIT = "__OMIT__";

  static public class XmlBuilderFactory extends BuilderFactory<XmlBuilder> {

    public XmlBuilderFactory(final TipitakaUrlFactory urlFactory, final File directoryMap)
        throws XmlPullParserException, IOException
    {
      super(urlFactory, directoryMap);
    }

    @Override
    public XmlBuilder create(final Writer writer) {
      return new XmlBuilder(writer, this);
    }
  }

  private final BuilderFactory<XmlBuilder> factory;

  public void flush() throws IOException {
    state.flush();
  }

  private State state;

  static public void main(String... args) throws Exception {
    BuilderFactory<XmlBuilder> factory = new XmlBuilderFactory(new TipitakaUrlFactory("file:../tipitaka-search/solr/tipitaka/"),
        new File("../tipitaka-search/solr/tipitaka/directory.map"));

    TipitakaVisitor visitor = new TipitakaVisitor(factory);

    visitor.accept(new OutputStreamWriter(System.out), new ScriptFactory().script("romn"),
        //    "/tipitaka (mula)/vinayapitaka/parajikapali/veranjakandam");
        "/tipitaka (mula)/vinayapitaka/parajikapali/1. parajikakandam");
        //visitor.generateAll(new File("archive"), new ScriptFactory().script("romn"));
    for(String i : Fuzzy.results) {
      System.out.println(i);
    }
  }

  public void generateAll(File basedir, Script script) throws IOException {
    for (String path : this.factory.getDirectory().allPaths()) {
      File index = new File(basedir, script.name + path + ".html");
      System.err.println(index+ " " + this.factory.getDirectory().fileOf(path));
      index.getParentFile().mkdirs();
      // TODO ensure utf-8
      Writer writer = new FileWriter(index);
      //accept(writer, script, path);
      writer.close();
    }
  }

  public XmlBuilder(Writer writer, BuilderFactory<XmlBuilder> factory) {
    this.factory = factory;
    this.state = new State(writer);
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
    state.append("<archivePath>").append("/").append(script.name).append(path).append(".xml</archivePath>\n");

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
    String lineNumber = state.nextNumber();
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
        state.append("<note>");
        state.appendText(text);
        state.append("</note>");
    }
    else {
      state.appendText(text.replaceFirst("^ *", ""));
    }
    ////writer.append("<n>").append(text).append("</n>");
    //if (text.contains("(")) {
    //  Pattern NOTE = Pattern.compile("^([^()]*)\\((([^()]+[.][^()]*)|\\?)\\)$");
    //  Matcher matcher = NOTE.matcher(text);
    //  if (matcher.matches()) {
    //    writer.append("<alternative>");
    //    String altText = matcher.group(1).trim();
    //    String[] words = previous.split(" ");
    //    int count = altText.replaceAll("[^ ]*", "").length() + 1;
    //    if (count == 1) {
    //      String pre = words[words.length - 1];
    //      if (new Fuzzy().similar(altText, pre)) {
    //        writer.append("<text>").append(pre.replace("‘‘", "")).append("</text>");
    //      }
    //    }
    //    if (count == 2) {
    //      String pre1 = words[words.length - 1];
    //      String pre2 = words[words.length - 2];
    //      String[] parts = altText.split(" ");
    //      if (new Fuzzy().similar(parts[0], pre2) && new Fuzzy().similar(parts[1], pre1)) {
    //        writer.append("<text>").append(pre2.replace("‘‘","")).append(" ").append(pre1.replace("‘‘","")).append(
    //            "</text>");
    //      }
    //      else if (new Fuzzy().similar(altText, pre1)) {
    //        writer.append("<text>").append(pre1.replace("‘‘","")).append("</text>");
    //      }
    //    }
    //    writer.append("<altText lang=\"").append(matcher.group(2)).append("\">").append(altText).append("</altText>");
    //    writer.append("</alternative>");
    //    //text = matcher.group(3);
    //  }
    //}
    //if (text.length() > 0) {
    //  writer.append("<note>");
    //  writer.append(text);
    //  writer.append("</note>");
    //}

  }

  public void noteEnd() throws IOException {
    state.pop();
  }

  public void pageBreak(String edition, String number) throws IOException {
    // TODO make enum for edition
    state.append("<pageBreak edition=\"").append(edition).append("\" number=\"").append(number).append("\" />");
  }
}
