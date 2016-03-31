package org.tipitaka.archive.servlets;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import org.tipitaka.archive.Layout;

/**
 * Created by cmeier on 3/31/16.
 */
class ServletLayout
    extends Layout
{
  private final File webInfDir;

  private final File dataDir;

  ServletLayout(ServletContext context) {
    File file = new File(context.getRealPath("WEB-INF/web.xml"));
    if (!file.exists()) {
      file = new File(context.getRealPath("directory.map"));
    }
    this.webInfDir = file.getParentFile();
    context.log("found script directory: " + webInfDir);
    String path = context.getInitParameter("data.dir");
    if (path != null) {
      file = new File(path);
    }
    else {
      file = new File(context.getRealPath("toc.xml")).getParentFile();
    }
    this.dataDir = file;
    context.log("found data directory: " + dataDir);
  }

  @Override
  public URL tipitakaOrgMirror() throws MalformedURLException {
    return null;
  }

  @Override
  public File notesArchive() {
    return null;
  }

  @Override
  public File dataArchive() {
    return dataDir;
  }

  @Override
  public File scriptDirectory() {
    return webInfDir;
  }
}
