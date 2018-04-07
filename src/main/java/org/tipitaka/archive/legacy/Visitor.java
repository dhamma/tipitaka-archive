package org.tipitaka.archive.legacy;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import org.tipitaka.archive.model.Document;

public class Visitor
{

  private final XmlPullParserFactory factory;

  public Visitor() throws IOException {
    try {
      this.factory = XmlPullParserFactory.newInstance();
    }
    catch(XmlPullParserException e) {
      throw new IOException(e);
    }
  }

  public void accept(Builder builder, String sourceUrl, Document document) throws IOException {
    URL url = new URL(sourceUrl);

    builder.documentStart(document);
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

  private void accept(Builder builder, XmlPullParser xpp) throws XmlPullParserException, IOException {
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

  private void visitStartTag(Builder builder, XmlPullParser xpp) throws XmlPullParserException, IOException {
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

  private void visitEndTag(Builder builder, XmlPullParser xpp) throws XmlPullParserException, IOException {
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
