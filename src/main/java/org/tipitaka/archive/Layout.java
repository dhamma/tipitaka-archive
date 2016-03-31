package org.tipitaka.archive;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by cmeier on 3/12/16.
 */
public class Layout
{

  public URL tipitakaOrgMirror() throws MalformedURLException {
    return new URL("file:../tipitaka-search/solr/tipitaka/");
  }

  public File notesArchive() {
    return new File("../tipitaka-archive/archive/notes");
  }

  public File dataArchive() {
    return new File("../tipitaka-archive/archive/data");
  }

  public File directoryMap() {
    return new File(scriptDirectory(), "directory.map");
  }

  public File scriptDirectory() {
    return new File("../tipitaka-archive/archive/");
  }
}
