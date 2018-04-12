package org.tipitaka.archive.legacy;

import java.io.IOException;

import org.tipitaka.archive.model.Document;

/**
 * Created by cmeier on 3/6/16.
 */
public interface Builder {

  void documentStart(Document document) throws IOException;

  void documentEnd() throws IOException;

  void highlightStart(String name) throws IOException;

  void highlightEnd() throws IOException;

  void paraStart(String name, String number) throws IOException;

  void paraEnd() throws IOException;

  void noteStart() throws IOException;

  void noteEnd() throws IOException;

  void pageBreak(String edition, String number) throws IOException;

  void text(String text) throws IOException;
}
