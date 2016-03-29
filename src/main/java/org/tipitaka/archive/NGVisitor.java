package org.tipitaka.archive;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import org.tipitaka.archive.Notes.Version;
import org.tipitaka.search.Script;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class NGVisitor implements Visitor<NGBuilder>
{

  private final XmlPullParserFactory factory;

  private final BuilderFactory<NGBuilder> builders;

  public NGVisitor(BuilderFactory<NGBuilder> builders) throws XmlPullParserException {
    this.factory = XmlPullParserFactory.newInstance();
    this.builders = builders;
  }

  public void accept(File basedir, Script script) throws IOException {
    for (String path : this.builders.getDirectory().allPaths()) {
      File file = new File(basedir, this.builders.archivePath(script, path).getPath());
      System.err.println(this.builders.getDirectory().fileOf(path) + " " + file);
      file.getParentFile().mkdirs();
      accept(file, script, path);
    }
  }

  public void accept(File file, Script script, String path) throws IOException {
    try (NGBuilder builder = builders.create(file, script, path)) {
      accept(builder, script, path);
    }
  }

  public void accept(Writer writer, Script script, String path) throws IOException {
    accept( builders.create(writer), script, path);
  }

  public void accept(NGBuilder builder, Script script, String path) throws IOException {
    URL url = new File(builders.getArchiveDirectory(), builders.archivePath(script, path).getPath()).toURI().toURL();

    try {
      XmlPullParser xpp = factory.newPullParser();
      try (Reader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
        xpp.setInput(reader);
        accept(builder, xpp);
      }
    }
    catch (XmlPullParserException e) {
      throw new IOException("pull parser error", e);
    }
    builder.flush();
  }

  private void accept(NGBuilder builder, XmlPullParser xpp) throws XmlPullParserException, IOException {
    int eventType = xpp.getEventType();
    while (eventType != XmlPullParser.END_DOCUMENT) {
      if (eventType == XmlPullParser.START_TAG) {
        visitStartTag(builder, xpp);
      }
      else if (eventType == XmlPullParser.TEXT) {
        visitText(builder, xpp.getText());
      }
      else if (eventType == XmlPullParser.END_TAG) {
        visitEndTag(builder, xpp);
      }
      eventType = xpp.next();
    }
    builder.flush();
  }

  enum State {
    NORMATIVE, ARCHIVE, TITLE, ALTERNATIVE
  }
  private State state = null;

  private void visitStartTag(NGBuilder builder, XmlPullParser xpp) throws XmlPullParserException, IOException {
    String name = xpp.getName();
    switch (name) {
      case "document":
        builder.startDocument();
        return;
      case "metadata":
        builder.startMetadata();
        return;
      case "normativeSource":
        state = State.NORMATIVE;
        return;
      case "archivePath":
        state = State.ARCHIVE;
        return;
      case "titlePath":
        state = State.TITLE;
        return;
      case "body":
        builder.startBody();
        return;
      case "nikaya":
        builder.startNikaya();
        return;
      case "book":
        builder.startBook();
        return;
      case "title":
        builder.startTitle();
        return;
      case "chapter":
        builder.startChapter();
        return;
      case "centered":
        builder.startCentered();
        return;
      case "subhead":
        builder.startSubhead();
        return;
      case "indent":
        builder.startIndent();
        return;
      case "bold":
        builder.startBold();
        return;
      case "gatha1":
      case "gatha2":
      case "gatha3":
      case "gathalast":
        builder.startGatha(name.substring(5));
        return;
      case "paragraph":
        String number = xpp.getAttributeValue(null, "number");
        builder.startParagraph(number);
        return;
      case "hangnum":
        number = xpp.getAttributeValue(null, "number");
        builder.startHangnum(number);
        return;
      case "note":
        builder.startNote();
        return;
      case "pageBreak":
        number = xpp.getAttributeValue(null, "number");
        String edition = xpp.getAttributeValue(null, "edition");
        builder.pageBreak(edition, number);
        return;
      case "alternatives":
        String extra = xpp.getAttributeValue(null, "extra");
        String separator = xpp.getAttributeValue(null, "separator");
        builder.startAlternatives(extra, "true".equals(separator));
        return;
      case "alternative":
        String source = xpp.getAttributeValue(null, "source-abbr");
        builder.beginAlternative(source);
        state = State.ALTERNATIVE;
        return;
      default:
        System.err.println("TODO " + name);
    }
  }

  private void visitText(NGBuilder builder, String text) throws XmlPullParserException, IOException {
    if (state == null) {
      builder.text(text);
      return;
    }
    switch (state) {
      case NORMATIVE:
        builder.normativeSource(text);
        return;
      case ARCHIVE:
        builder.archivePath(text);
        return;
      case TITLE:
        builder.title(text);
        return;
      case ALTERNATIVE:
        builder.finalizeAlternative(text);
        return;
    }
}

  private void visitEndTag(NGBuilder builder, XmlPullParser xpp) throws XmlPullParserException, IOException {
    state = null;
    String name = xpp.getName();
    switch (name) {
      case "document":
        builder.endDocument();
        return;
      case "metadata":
        builder.endMetadata();
        return;
      case "normativeSource":
      case "archivePath":
      case "titlePath":
        return;
      case "body":
        builder.endBody();
        return;
      case "nikaya":
        builder.endNikaya();
        return;
      case "book":
        builder.endBook();
        return;
      case "chapter":
        builder.endChapter();
        return;
      case "centered":
        builder.endCentered();
        return;
      case "subhead":
        builder.endSubhead();
        return;
      case "title":
        builder.endTitle();
        return;
      case "indent":
        builder.endIndent();
        return;
      case "bold":
        builder.endBold();
        return;
      case "gatha1":
      case "gatha2":
      case "gatha3":
      case "gathalast":
        builder.endGatha(name.substring(5));
        return;
      case "paragraph":
        builder.endParagraph();
        return;
      case "hangnum":
        builder.endHangnum();
        return;
      case "note":
        builder.endNote();
        return;
      case "pageBreak":
        return;
      case "alternatives":
        builder.endAlternatives();
        return;
      case "alternative":
        state = null;
        return;
      default:
        System.err.println("TODO " + name);
    }
  }

}
