package org.tipitaka.archive;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by cmeier on 2/28/16.
 */
public interface Visitor
{
  void accept(Writer writer, String path, String... args) throws IOException;
}
