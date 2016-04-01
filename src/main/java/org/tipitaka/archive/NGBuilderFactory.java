package org.tipitaka.archive;

import java.io.File;
import java.io.IOException;

/**
 * Created by cmeier on 4/1/16.
 */
public abstract class NGBuilderFactory
    extends BuilderFactory<NGBuilder>
{

  public NGBuilderFactory(final Layout layout) throws IOException {
    super(layout);
  }

  public NGBuilderFactory() throws IOException {
  }
}
