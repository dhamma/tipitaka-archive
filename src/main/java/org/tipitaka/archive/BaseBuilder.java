package org.tipitaka.archive;

import java.io.Closeable;
import java.io.IOException;

import org.tipitaka.search.Script;

/**
 * Created by cmeier on 3/25/16.
 */
public interface BaseBuilder extends Closeable
{
  void flush() throws IOException;

  void text(String text) throws IOException;
}
