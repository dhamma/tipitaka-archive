package org.tipitaka.archive;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

import org.tipitaka.search.DirectoryStructure;
import org.tipitaka.search.TipitakaUrlFactory;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Created by cmeier on 3/6/16.
 */
abstract class BuilderFactory<T extends Builder>
{

  private final DirectoryStructure directory;

  private final TipitakaUrlFactory urlFactory;

  public BuilderFactory(TipitakaUrlFactory urlFactory, File directoryMap)
      throws XmlPullParserException, IOException
  {
    this.urlFactory = urlFactory;
    this.directory = new DirectoryStructure(urlFactory);
    if (directoryMap == null) {
      this.directory.reload();
    }
    else {
      this.directory.load(directoryMap);
    }
  }

  public DirectoryStructure getDirectory() {
    return directory;
  }

  public TipitakaUrlFactory getUrlFactory() {
    return urlFactory;
  }

  public abstract T create(Writer writer);
}
