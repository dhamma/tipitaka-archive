package org.tipitaka.archive;

import java.io.File;
import java.io.IOException;

/**
 * Created by cmeier on 4/1/16.
 */
public abstract class LegacyBuilderFactory extends BuilderFactory<LegacyBuilder>
{

  public LegacyBuilderFactory(final Layout layout) throws IOException {
    super(layout);
  }

  public LegacyBuilderFactory() throws IOException {
  }

  public abstract LegacyBuilder create(File file, Script script, String path) throws IOException;

  public abstract File archivePath(Script script, String path);
}
