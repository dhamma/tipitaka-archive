package org.tipitaka.archive;

import java.io.IOException;
import java.io.Writer;

import org.tipitaka.search.Script;

/**
 * Created by cmeier on 2/28/16.
 */
public interface Visitor
{
  void accept(Builder builder, Script script, String path) throws IOException;
}
