package org.tipitaka.archive;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.tipitaka.search.Script;

/**
 * Created by cmeier on 3/6/16.
 */
public interface Builder extends Closeable
{

  void flush() throws IOException;

  void documentStart(Script script, String path) throws IOException;

  void documentEnd() throws IOException;

  void highlightStart(String name) throws IOException;

  void highlightEnd() throws IOException;

  void paraStart(String name, String number) throws IOException;

  void paraEnd() throws IOException;

  void noteStart() throws IOException;

  void text(String text) throws IOException;

  void noteEnd() throws IOException;

  void pageBreak(String edition, String number) throws IOException;
}
