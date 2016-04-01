package org.tipitaka.archive;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

/**
 * Created by cmeier on 3/6/16.
 */
public abstract class BuilderFactory<T extends BaseBuilder>
{

  private final DirectoryStructure directory;

  private final TipitakaUrlFactory urlFactory;

  private final File archiveDirectory;

  public BuilderFactory() throws IOException {
    this(new Layout());
  }

  public BuilderFactory(Layout layout) throws IOException
  {
    this.urlFactory = new TipitakaUrlFactory(layout.tipitakaOrgMirror());
    this.directory = new DirectoryStructure(urlFactory);
    this.directory.load(layout.directoryMap());
    this.archiveDirectory = layout.dataArchive();
  }

  public File getArchiveDirectory() {
    return archiveDirectory;
  }

  public DirectoryStructure getDirectory() {
    return directory;
  }

  public TipitakaUrlFactory getUrlFactory() {
    return urlFactory;
  }

  public abstract T create(Writer writer);

}
