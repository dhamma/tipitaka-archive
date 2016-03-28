package org.tipitaka.archive;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import org.tipitaka.search.Script;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class LegacyVisitor implements Visitor<LegacyBuilder>
{

  private final XmlPullParserFactory factory;

  private final BuilderFactory<LegacyBuilder> builders;

  public LegacyVisitor(BuilderFactory factory) throws XmlPullParserException {
    this.factory = XmlPullParserFactory.newInstance();
    this.builders = factory;
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
    try (LegacyBuilder builder = builders.create(file, script, path)) {
      accept(builder, script, path);
    }
  }

  public void accept(Writer writer, Script script, String path) throws IOException {
    accept(builders.create(writer), script, path);
  }

  public void accept(LegacyBuilder builder, Script script, String path) throws IOException {
    URL url = builders.getUrlFactory().sourceURL(script, builders.getDirectory().fileOf(path));

    builder.documentStart(script, path);
    Reader reader = null;
    try {
      XmlPullParser xpp = factory.newPullParser();
      try {
        reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-16"));
        xpp.setInput(reader);
        accept(builder, xpp);
      }
      catch (XmlPullParserException e) {
        if (reader != null) {
          reader.close();
        }
        reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
        xpp.setInput(reader);
        accept(builder, xpp);
      }
    }
    catch (XmlPullParserException e) {
      throw new IOException("pull parser error", e);
    }
    builder.documentEnd();
    builder.flush();
  }

  private void accept(LegacyBuilder builder, XmlPullParser xpp) throws XmlPullParserException, IOException {
    int eventType = xpp.getEventType();
    while (eventType != XmlPullParser.END_DOCUMENT) {
      if (eventType == XmlPullParser.START_TAG) {
        visitStartTag(builder, xpp);
      }
      else if (eventType == XmlPullParser.TEXT) {
        builder.text(xpp.getText());
      }
      else if (eventType == XmlPullParser.END_TAG) {
        visitEndTag(builder, xpp);
      }
      eventType = xpp.next();
    }
    builder.flush();
  }

  private void visitStartTag(LegacyBuilder builder, XmlPullParser xpp) throws XmlPullParserException, IOException {
    if (xpp.getName().equals("pb")) {
      builder.pageBreak(xpp.getAttributeValue(null, "ed"), xpp.getAttributeValue(null, "n"));
    }
    else if (xpp.getName().equals("note")) {
      builder.noteStart();
    }
    else if (xpp.getName().equals("p")) {
      String name = xpp.getAttributeValue(null, "rend");
      String number = xpp.getAttributeValue(null, "n");
      builder.paraStart(name, number);
    }
    else if (xpp.getName().equals("hi")) {
      String name = xpp.getAttributeValue(null, "rend");
      builder.highlightStart(name);
    }
    else {
      //System.err.println(xpp.getName());
    }
  }

  private void visitEndTag(LegacyBuilder builder, XmlPullParser xpp) throws XmlPullParserException, IOException {
    if (xpp.getName().equals("note")) {
      builder.noteEnd();
    }
    else if (xpp.getName().equals("p")) {
      builder.paraEnd();
    }
    else if (xpp.getName().equals("hi")) {
      builder.highlightEnd();
    }
  }

}
