package org.tipitaka.archive;

import java.io.IOException;

/**
 * Created by cmeier on 3/6/16.
 */
public interface LegacyBuilder
    extends BaseBuilder
{

  void documentStart(Script script, String path) throws IOException;

  void documentEnd() throws IOException;

  void highlightStart(String name) throws IOException;

  void highlightEnd() throws IOException;

  void paraStart(String name, String number) throws IOException;

  void paraEnd() throws IOException;

  void noteStart() throws IOException;

  void noteEnd() throws IOException;

  void pageBreak(String edition, String number) throws IOException;
}
