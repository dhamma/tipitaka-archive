package org.tipitaka.archive;

import java.io.IOException;

/**
 * Created by cmeier on 2/28/16.
 */
public interface Visitor<T extends BaseBuilder>
{
  void accept(T builder, Script script, String path) throws IOException;
}
