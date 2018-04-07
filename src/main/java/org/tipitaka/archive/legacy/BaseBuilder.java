package org.tipitaka.archive.legacy;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by cmeier on 3/25/16.
 */
public interface BaseBuilder extends Closeable
{
  void flush() throws IOException;

  void text(String text) throws IOException;
}
