package org.tipitaka.archive;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tipitaka.search.Builder;
import org.tipitaka.search.DirectoryStructure;
import org.tipitaka.search.Script;
import org.tipitaka.search.ScriptFactory;
import org.tipitaka.search.TipitakaOrgVisitor;
import org.tipitaka.search.TipitakaUrlFactory;
import org.xmlpull.v1.XmlPullParserException;

public class TipitakaOrgVisitorXml
    extends TipitakaOrgVisitor
{

  static class State
  {
    boolean isAnchor = false;

    int count = 0;

    Builder builder;

    String nextNumber() {
      count++;
      return String.valueOf(count);
    }

    Writer append(CharSequence string) throws IOException {
      return builder.append(string);
    }
  }

  private final DirectoryStructure directory;

  private State state = new State();

  public TipitakaOrgVisitorXml(TipitakaUrlFactory urlFactory) throws XmlPullParserException, IOException {
    this(urlFactory, null);
  }

  public TipitakaOrgVisitorXml(TipitakaUrlFactory urlFactory, File directoryMap)
      throws XmlPullParserException, IOException
  {
    super(urlFactory);
    this.directory = new DirectoryStructure(urlFactory);
    if (directoryMap == null) {
      this.directory.reload();
    }
    else {
      this.directory.load(directoryMap);
    }
  }

  static public void main(String... args) throws Exception {
    TipitakaOrgVisitorXml visitor = new TipitakaOrgVisitorXml(new TipitakaUrlFactory("file:../tipitaka-search/solr/tipitaka/"),
        new File("../tipitaka-search/solr/tipitaka/directory.map"));

    visitor.accept(new OutputStreamWriter(System.out), new ScriptFactory().script("romn"),
        //    "/tipitaka (mula)/vinayapitaka/parajikapali/veranjakandam");
        "/tipitaka (mula)/vinayapitaka/parajikapali/1. parajikakandam");
    //visitor.generateAll(new File("archive"), new ScriptFactory().script("romn"));
    new Fuzzy().similar("asdqweasd", "asdwerwerrwerasd");
    new Fuzzy().similar("asdqwead", "werwerrwerasd");
    new Fuzzy().similar("dārukuṭikaṃ", "dārukuḍḍikaṃkuṭikaṃ");
    new Fuzzy().similar("Icchāmi", "dārukuḍḍikaṃ");
    new Fuzzy().similar("dārukuṭikaṃ", "kuṭikaṃ");
    for(String i : Fuzzy.results) {
      System.out.println(i);
    }
  }

  public void generateAll(File basedir, Script script) throws IOException {
    for (String path : this.directory.allPaths()) {
      File index = new File(basedir, script.name + path + ".html");
      System.err.println(index+ " " + directory.fileOf(path));
      index.getParentFile().mkdirs();
      // TODO ensure utf-8
      Writer writer = new FileWriter(index);
      accept(writer, script, path);
      writer.close();
    }
  }

  public void accept(Writer writer, Script script, String path) throws IOException {
    String legacy = this.directory.fileOf(path);
    URL url = urlFactory.normativeURL(script, legacy);
    state = new State();
    state.builder = new Builder(this.directory, script, writer, path, url.toString());
    super.accept(writer, script, legacy);
  }

  protected void docStart(Writer writer, String url) throws IOException {
    state.builder.startXmlBody();
    writer.append("<content>");
  }

  protected void docEnd(Writer writer) throws IOException {
    state.append("</content>");
    state.builder.endXmlBody();
  }

  protected String hiStart(Writer writer, String clazz) throws IOException {
    if ("paranum".equals(clazz) || "dot".equals(clazz)) {
      return OMIT;
    }
    writer.append("<").append(clazz).append(">");
    return clazz;
  }

  protected void hiEnd(Writer writer, String clazz) throws IOException {
    if (OMIT.equals(clazz)){
      return;
    }
    writer.append("</").append(clazz).append(">");
  }

  protected void pStart(Writer writer, String clazz, String number) throws IOException {
    String lineNumber = state.nextNumber();
    if ("bodytext".equals(clazz)) {
      clazz = "paragraph";
    }
    state.append("<").append(clazz).append(" line=\"").append(lineNumber).append("\"");
    if (number != null) {
      state.append(" number=\"").append(number).append("\"");
    }
    state.append(">");
  }

  protected void pEnd(Writer writer, String clazz) throws IOException {
    if ("bodytext".equals(clazz)) {
      clazz = "paragraph";
    }
    writer.append("</").append(clazz).append(">");
  }

  protected void noteStart(Writer writer) throws IOException {
    //writer.append("<note>");
  }

  @Override
  protected void noteText(final Writer writer, String previous, String text) throws IOException {
    //writer.append("<n>").append(text).append("</n>");
    if (text.contains("(")) {
      Pattern NOTE = Pattern.compile("^([^()]*)\\((([^()]+[.][^()]*)|\\?)\\)$");
      Matcher matcher = NOTE.matcher(text);
      if (matcher.matches()) {
        writer.append("<alternative>");
        String altText = matcher.group(1).trim();
        String[] words = previous.split(" ");
        int count = altText.replaceAll("[^ ]*", "").length() + 1;
        if (count == 1) {
          String pre = words[words.length - 1];
          if (new Fuzzy().similar(altText, pre)) {
            writer.append("<text>").append(pre.replace("‘‘", "")).append("</text>");
          }
        }
        if (count == 2) {
          String pre1 = words[words.length - 1];
          String pre2 = words[words.length - 2];
          String[] parts = altText.split(" ");
          if (new Fuzzy().similar(parts[0], pre2) && new Fuzzy().similar(parts[1], pre1)) {
            writer.append("<text>").append(pre2.replace("‘‘","")).append(" ").append(pre1.replace("‘‘","")).append(
                "</text>");
          }
          else if (new Fuzzy().similar(altText, pre1)) {
            writer.append("<text>").append(pre1.replace("‘‘","")).append("</text>");
          }
        }
        writer.append("<altText lang=\"").append(matcher.group(2)).append("\">").append(altText).append("</altText>");
        writer.append("</alternative>");
        //text = matcher.group(3);
      }
    }
    if (text.length() > 0) {
      writer.append("<note>");
      writer.append(text);
      writer.append("</note>");
    }

  }

  protected void noteEnd(Writer writer) throws IOException {
  }

  protected void pb(Writer writer, String ed, String n) throws IOException {
    // TODO make enum for edition
    writer.append("<marker edition=\"").append(ed).append("\" number=\"").append(n).append("\" />");
  }
}
