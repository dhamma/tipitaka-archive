package org.tipitaka.archive;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

import org.tipitaka.search.DirectoryStructure;
import org.tipitaka.search.Script;
import org.tipitaka.search.TipitakaUrlFactory;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Created by cmeier on 3/6/16.
 */
abstract class BuilderFactory<T extends Builder>
{

  private final DirectoryStructure directory;

  private final TipitakaUrlFactory urlFactory;

  public BuilderFactory() throws IOException {
    this(new Layout());
  }

  public BuilderFactory(Layout layout) throws IOException
  {
    this.urlFactory = new TipitakaUrlFactory(layout.tipitakaOrgMirror());
    this.directory = new DirectoryStructure(urlFactory);
    this.directory.load(layout.directoryMap());
  }

  public DirectoryStructure getDirectory() {
    return directory;
  }

  public TipitakaUrlFactory getUrlFactory() {
    return urlFactory;
  }

  public abstract T create(Writer writer);

  public abstract T create(final File file, final Script script, final String path) throws IOException;

  public abstract File archivePath(final Script script, final String path);
}
